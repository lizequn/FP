package uk.ac.ncl.cs.zequn.fp.net.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Zequn on 2014/5/11.
 */
@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e){
        e.printStackTrace();
        return e.getMessage();
    }
}
