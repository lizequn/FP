package uk.ac.ncl.cs.zequn.fp.net.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.ResultOutput;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.Strategy;
import uk.ac.ncl.cs.zequn.fp.net.service.Eba2LAService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Zequn on 2014/5/11.
 */
@Controller
@RequestMapping(value = "begin")
public class ServiceBeginController {
    @Autowired
    private Eba2LAService eba2LAService;
    @RequestMapping(value = "/{strategy}/{slice}/{range}")
    @ResponseBody
    public int createService(@PathVariable String strategy,@PathVariable long slice,@PathVariable long range) throws IOException, SQLException {
        Strategy strategy1 = Strategy.valueOf(strategy.toUpperCase());
        if(null == strategy1) throw new IllegalArgumentException("strategy name "+strategy +"not exists");
        String str = eba2LAService.beginService(strategy1,slice,range,new ResultOutput() {
            @Override
            public void output(String string) {
                //ignore
            }
        });
        return Integer.valueOf(str);
    }

//    @RequestMapping(value = "/{services}")
//    @ResponseBody
//    public int stopService(@PathVariable String services) throws IOException, SQLException {
//        eba2LAService.stopService(services);
//        return 1;
//    }
}
