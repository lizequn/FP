package uk.ac.ncl.cs.zequn.fp.eba2LA.monitor;

import uk.ac.ncl.cs.zequn.fp.net.entity.ResultSet;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Zequn on 2014/5/12.
 */
public interface ResultDispatcherListener {
    void transferResult(String set);
}
