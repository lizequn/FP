package uk.ac.ncl.cs.zequn.fp.net.exception;

/**
 * Created by Zequn on 2014/5/11.
 */
public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String str){
        super("service id :"+str+"is not correct");
    }
}
