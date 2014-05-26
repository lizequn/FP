package uk.ac.ncl.cs.zequn.fp.eba2LA.monitor;

import uk.ac.ncl.cs.zequn.fp.eba2LA.core.Config;
import uk.ac.ncl.cs.zequn.fp.eba2LA.filesystem.LogAccess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ZequnLi
 *         Date: 14-4-3
 */
public class ResultMonitorImpl implements ResultMonitor {
    private final long max = Runtime.getRuntime().maxMemory();
    private final long interval;
    private long counter= 0;
    private AtomicReference<Boolean> flag = new AtomicReference<Boolean>();
    private long inputNum;
    private long disk;
    private long latencyBefore;
    private long totalLatency;
    private long latencyNum;
    private long latencyBefore4Result;
    private long totalLatency4Result;
    private long latencyNum4Result;
    private long latencyBefore4GetResult;
    private long latencyNum4GetResult;
    private long totalLatency4GetResult;

    private final Object lock = new Object();
    private final Object diskLock = new Object();
    private final Object latencyLock4Result = new Object();
    private final Object latencyLock = new Object();
    private final Object latencyLock4GetResult = new Object();
    private final List<ResultMonitorListener> listenerList = new ArrayList<ResultMonitorListener>();
    private final LogAccess logAccess;
    private final LogAccess diskLog;
    private final LogAccess latencyLog4Result;
    private final LogAccess latencyLog4Stream;
    private final LogAccess latencyLog4GetResult;
    public ResultMonitorImpl(long interval, LogAccess logAccess, LogAccess logAccess1, LogAccess logAccess2, LogAccess logAccess3,LogAccess logAccess4) throws SQLException {
        this.interval = interval;
        this.logAccess = logAccess;
        diskLog = logAccess1;
        latencyLog4Result = logAccess2;
        latencyLog4Stream= logAccess3;
        latencyLog4GetResult = logAccess4;
        if(null!=logAccess){
            this.logAccess.init();
        }
        if(null!=diskLog){
            this.diskLog.init();
        }
        if(null!=latencyLog4Result){
            this.latencyLog4Result.init();
        }
        if(null!=latencyLog4Stream){
            this.latencyLog4Stream.init();
        }
        if(null!=latencyLog4GetResult){
            this.latencyLog4GetResult.init();
        }

        flag.set(true);
    }
    public void addListener(ResultMonitorListener listener){
        listenerList.add(listener);
    }




    @Override
    public void inputRateCount(){
        synchronized(lock){
            inputNum++;
        }
    }
    @Override
    public void diskCount(){
        synchronized (diskLock){
            disk++;
        }
    }

    @Override
    public void latencyBefore4Result() {
        synchronized (latencyLock4Result){
            latencyBefore4Result = System.nanoTime();
        }
    }

    @Override
    public void latencyAfter4Result() {
        synchronized (latencyLock4Result){
            totalLatency4Result += System.nanoTime()-latencyBefore4Result;
            latencyNum4Result++;
        }
    }
    @Override
    public void latencyBefore4Stream(){
        synchronized (latencyLock){
            latencyBefore = System.nanoTime();
        }
    }
    @Override
    public void latencyAfter4Stream(){
        synchronized (latencyLock){
            totalLatency += System.nanoTime()-latencyBefore;
            latencyNum++;
        }
    }

    @Override
    public void latencyBefore4GetResult() {
        synchronized (latencyLock4GetResult){
            latencyBefore4GetResult = System.nanoTime();
        }
    }

    @Override
    public void latencyAfter4GetResult() {
        synchronized (latencyLock4GetResult){
            totalLatency4GetResult+=System.nanoTime()-latencyBefore4GetResult;
            latencyNum4GetResult++;
        }

    }

    public void start(){
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private Runnable runnable = new Runnable() {
       @Override
       public void run() {
           while (flag.get()){
               long free = Runtime.getRuntime().maxMemory()-Runtime.getRuntime().freeMemory();

//               System.out.println("Max Heap Size: " + B2M.byte2M(max));
//               System.out.println("Current Free size: "+B2M.byte2M(free));
               counter++;
               synchronized (lock){
                   if(null!= logAccess){
                       logAccess.insertTuple(counter+"",B2M.byte2M(free)+"",(double)inputNum/(interval/1000) +"");
                       System.out.println("input rate: "+ (double)inputNum/(interval/1000));
                       inputNum =0;
                   }
               }

               synchronized (diskLock){
                   if(null != diskLog){
                       diskLog.insertTuple(counter+"",disk+"");
                       System.out.println("disk Read rate: "+ (double)disk/(interval/1000));
                       disk =0;
                   }

               }
               synchronized (latencyLock){
                   if(null!= latencyLog4Stream){
                       long average;
                       if(latencyNum == 0){
                            average = 0;
                       }else {
                            average =  TimeUnit.MICROSECONDS.convert(totalLatency / latencyNum,TimeUnit.NANOSECONDS);
                       }
                       latencyLog4Stream.insertTuple(counter+"",average+"");
                       System.out.println("average Latency 4 stream: "+average);
                       totalLatency = 0;
                       latencyNum = 0;
                   }

               }
               synchronized (latencyLock4Result){
                   if(null!= latencyLog4Result){
                       long average;
                       if(latencyNum4Result == 0){
                           average = 0;
                       }else {
                           average =  TimeUnit.MICROSECONDS.convert(totalLatency4Result / latencyNum4Result,TimeUnit.NANOSECONDS);
                       }
                       latencyLog4Result.insertTuple(counter+"",average+"");
                       System.out.println("average Latency 4 result: "+average);
                       totalLatency4Result = 0;
                       latencyNum4Result = 0;
                   }
               }
               synchronized (latencyLock4GetResult){
                   if(null!= latencyLog4GetResult){
                       long average;
                       if(latencyNum4GetResult == 0){
                           average = 0;
                       }else {
                           average =  TimeUnit.MICROSECONDS.convert(totalLatency4GetResult / latencyNum4GetResult,TimeUnit.NANOSECONDS);
                       }
                       latencyLog4GetResult.insertTuple(counter+"",average+"");
                       System.out.println("average Latency 4 result: "+average);
                       totalLatency4GetResult = 0;
                       latencyNum4GetResult = 0;
                   }
               }
               if(listenerList.size()>0){
                   for(ResultMonitorListener listener : listenerList){
                       listener.monitor();
                   }
               }
               try {
                   Thread.sleep(interval);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
   };

    public void flushLog(){
        flag.set(false);
        String storeUrl = Config.CSVUrl4Win;
        if(null!= logAccess){
            logAccess.output2CSV(storeUrl,logAccess.getTable());
        }
        if(null!= diskLog){
            diskLog.output2CSV(storeUrl,diskLog.getTable());
        }
        if(null!=latencyLog4Stream){
            latencyLog4Stream.output2CSV(storeUrl,latencyLog4Stream.getTable());
        }
        if(null!= latencyLog4Result){
            latencyLog4Result.output2CSV(storeUrl,latencyLog4Result.getTable());
        }
        if(null!=latencyLog4GetResult){
            latencyLog4GetResult.output2CSV(storeUrl,latencyLog4GetResult.getTable());
        }

    }
}
