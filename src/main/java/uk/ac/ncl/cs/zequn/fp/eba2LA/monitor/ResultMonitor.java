package uk.ac.ncl.cs.zequn.fp.eba2LA.monitor;

/**
 * Created by Zequn on 2014/5/10.
 */
public interface ResultMonitor {
    void inputRateCount();
    void diskCount();
    void latencyBefore4Result();
    void latencyAfter4Result();
    void latencyBefore4Stream();
    void latencyAfter4Stream();
    void start();
    void flushLog();
    void addListener(ResultMonitorListener listener);
}
