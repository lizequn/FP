package uk.ac.ncl.cs.zequn.fp.net.service;

import uk.ac.ncl.cs.zequn.fp.eba2LA.core.ResultOutput;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.Strategy;
import uk.ac.ncl.cs.zequn.fp.net.entity.Tuple4net;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Zequn on 2014/5/11.
 */
public interface Eba2LAService {
    String beginService(Strategy strategy,long slide,long range,ResultOutput resultOutput) throws IOException, SQLException;
    void offer(String services,Tuple4net tuple4net);
    void offer(String services,long timestamp,double info);
    void stopService(String services);
}

