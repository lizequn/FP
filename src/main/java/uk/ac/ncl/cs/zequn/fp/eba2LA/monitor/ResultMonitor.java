package uk.ac.ncl.cs.zequn.fp.eba2LA.monitor;

/**
 * Created by Zequn on 2014/5/10.
 */
public interface ResultMonitor {
    void inputRateCount();
    void diskReadCount();
    void diskWriteCount();
    void latencyBefore();
    void latencyAfter();
    void start();
    void flushLog();
    void addListener(ResultMonitorListener listener);
}
