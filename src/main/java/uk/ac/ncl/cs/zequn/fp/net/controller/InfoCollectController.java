package uk.ac.ncl.cs.zequn.fp.net.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.ResultOutput;
import uk.ac.ncl.cs.zequn.fp.net.entity.Tuple4net;
import uk.ac.ncl.cs.zequn.fp.net.service.Eba2LAService;

/**
 * Created by Zequn on 2014/5/11.
 */

@Controller
@RequestMapping(value = "info")
public class InfoCollectController {
    @Autowired
    private Eba2LAService service;
    @RequestMapping(value = "/{services}/{time]/{info}")
    @ResponseBody
    public int infoCollect(@PathVariable String services,@PathVariable long time,@PathVariable double info){
        service.offer(services,time,info);
        return 1;
    }
    @RequestMapping(value = "/{services}",method = RequestMethod.POST)
    @ResponseBody
    public int infoCollect(@PathVariable String services,@RequestBody Tuple4net tuple4net){
        service.offer(services,tuple4net);
        return 1;
    }
}
