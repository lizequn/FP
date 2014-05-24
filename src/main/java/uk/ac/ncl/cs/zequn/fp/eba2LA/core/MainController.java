package uk.ac.ncl.cs.zequn.fp.eba2LA.core;

import uk.ac.ncl.cs.zequn.fp.eba2LA.avg.AvgCalculateImpl;
import uk.ac.ncl.cs.zequn.fp.eba2LA.filesystem.LogAccess;
import uk.ac.ncl.cs.zequn.fp.eba2LA.model.Tuple;
import uk.ac.ncl.cs.zequn.fp.eba2LA.monitor.ResultDispatcherListener;
import uk.ac.ncl.cs.zequn.fp.eba2LA.monitor.ResultMonitor;
import uk.ac.ncl.cs.zequn.fp.eba2LA.monitor.ResultMonitorImpl;
import uk.ac.ncl.cs.zequn.fp.eba2LA.monitor.ResultMonitorListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ZequnLi
 *         Date: 14-4-21
 */
public class MainController {
    private final InMemoryStore inMemoryStore;
    private final Strategy strategy;
    private final TupleFactory factory;
    private final Calculate calculate;
    private final long time;
    private final long period;
    private final Timer timer;
    private final TimerTask timerTask;
    private final long numOfTuples;
    private final AtomicReference<Double> resultList;
    private final AtomicReference<String> resultStore = new AtomicReference<String>();
    private final ResultOutput resultOutput;
    private boolean calFlag = false;
    private ResultMonitor resultMonitor = new ResultMonitorImpl(1000,new LogAccess("memory"),new LogAccess("disk"),new LogAccess("latency4Result"),new LogAccess("latency"));
    //private  ResultMonitor resultMonitor = new ResultMonitorImpl(1000,null,null,null,null);

    public MainController(Strategy strategy,long time,long period,ResultOutput resultOutputListener) throws SQLException, IOException {
        this.resultOutput = resultOutputListener;
        this.time = time;
        this.period = period;
        this.numOfTuples = period/time;
        //define max tuple in memory
        inMemoryStore = new InMemoryStore(false ,10*60*1, resultMonitor);
        timer = new Timer();
        this.strategy = strategy;
        switch (strategy){
            case AVG:
                calculate = new AvgCalculateImpl();break;
            default:
                throw new IllegalStateException();
        }
        factory = new TupleFactory(calculate);
        this.resultList = new AtomicReference<Double>();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                resultMonitor.latencyBefore4Result();
                Tuple newTuple = factory.getResult();
                if(newTuple == null) return;
                Tuple oldTuple = null;
                inMemoryStore.put(newTuple);
                if(inMemoryStore.getSize()>numOfTuples){
                    //System.out.println("remove !!!!!!!!!!!!!!"+inMemoryStore.getSize());
                    oldTuple = inMemoryStore.get();
                }
                if(resultList.get() != null){
                    resultList.set(calculate.updateResult(resultList.get(), inMemoryStore.getRealSize(), newTuple, oldTuple));
                } else {
                    resultList.set(calculate.updateResult(-1, inMemoryStore.getRealSize(), newTuple, oldTuple));
                }

                //resultOutput.output(calculate.getResult(resultList.get(),inMemoryStore.getRealSize())+"");
                resultStore.set(calculate.getResult(resultList.get(),inMemoryStore.getRealSize()));
                resultMonitor.latencyAfter4Result();

            }
        };
//        garbage collect
//        TimerTask gc = new TimerTask() {
//            @Override
//            public void run() {
//                System.gc();
//            }
//        };
//        new Timer().scheduleAtFixedRate(gc,0,1000*60*5);

        resultMonitor.addListener(new ResultMonitorListener() {
            @Override
            public void monitor() {
                System.out.println("total input:"+inMemoryStore.getRealSize());

            }
        });
        resultMonitor.addListener(new ResultMonitorListener() {
            @Override
            public void monitor() {
                System.out.println("total tuples:"+inMemoryStore.getSize());
            }
        });
        resultMonitor.start();
    }
    public void setMonitor(ResultMonitor resultMonitor){
        this.resultMonitor =  resultMonitor;
    }
    public String getResult(){
        return resultStore.get();
    }
    public void offer(double input){
        resultMonitor.latencyBefore4Stream();
        if(!calFlag) {
            calFlag = true;
            timer.scheduleAtFixedRate(timerTask,0,time);
        }
        factory.offer(input);
        resultMonitor.inputRateCount();
        resultMonitor.latencyAfter4Stream();
    }

    public void end(){
        timer.cancel();
        resultMonitor.flushLog();
    }
}
