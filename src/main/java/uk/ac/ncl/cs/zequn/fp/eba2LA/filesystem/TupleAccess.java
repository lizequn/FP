package uk.ac.ncl.cs.zequn.fp.eba2LA.filesystem;

import uk.ac.ncl.cs.zequn.fp.eba2LA.model.Tuple;

/**
 * @author ZequnLi
 *         Date: 14-5-4
 */
public interface TupleAccess {
    void insertTuple(Tuple tuple);
    Tuple getTuple();
    void init();
}
