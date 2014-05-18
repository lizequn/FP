package uk.ac.ncl.cs.zequn.fp.net.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ncl.cs.zequn.fp.net.entity.ResultSet;
import uk.ac.ncl.cs.zequn.fp.net.service.Eba2LAService;

/**
 * Created by Zequn on 2014/5/11.
 */
@Controller
@RequestMapping(value = "result")
public class GetResultController {
    @Autowired
    private Eba2LAService service;
    @RequestMapping(value = "/{services}")
    @ResponseBody
    public ResultSet infoCollect(@PathVariable String services){
        ResultSet resultSet = new ResultSet();
        resultSet.setInfo(service.getResult(services));
        return resultSet;
    }
    @RequestMapping(value = "/test/testinfo")
    @ResponseBody
    public int infoCollect(){
        return service.getTestInfo();
    }
}
