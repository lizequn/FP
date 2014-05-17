package uk.ac.ncl.cs.zequn.fp.net.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.MainController;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.ResultOutput;
import uk.ac.ncl.cs.zequn.fp.eba2LA.core.Strategy;
import uk.ac.ncl.cs.zequn.fp.eba2LA.monitor.ResultDispatcherListener;
import uk.ac.ncl.cs.zequn.fp.net.entity.Tuple4net;
import uk.ac.ncl.cs.zequn.fp.net.exception.ServiceNotFoundException;
import uk.ac.ncl.cs.zequn.fp.net.service.Eba2LAService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zequn on 2014/5/11.
 */
@Service
public class Eba2LAServiceImpl implements Eba2LAService {
    private Map<String,MainController> mainControllerList = new HashMap<String, MainController>();
    private static int counter = 0;
    private static int realInput = 0;




    @Override
    public String beginService(Strategy strategy, long slide, long range,ResultOutput resultOutput) throws IOException, SQLException {
        counter++;
        MainController mainController = new MainController(Strategy.AVG,slide,range,resultOutput);
        mainControllerList.put(counter+"",mainController);
        return counter+"";
    }

    @Override
    public void offer(String services,Tuple4net tuple4net) {
        this.offer(services,tuple4net.getTimestamp(),tuple4net.getInfo());
    }

    @Override
    public void offer(String services,long timestamp, double info) {
        String [] list = services.split("-");
        for(String id:list){
            MainController mainController = mainControllerList.get(id);
            if(null == mainController) throw new ServiceNotFoundException(id);
            mainController.offer(info);
        }
    }

    @Override
    public void stopService(String services) {
        String [] list = services.split("-");
        for(String id:list){
            MainController mainController = mainControllerList.get(id);
            if(null == mainController) throw new ServiceNotFoundException(id);
            mainController.end();
        }
    }

    @Override
    public String getResult(String services) {
        StringBuilder stringBuilder = new StringBuilder();
        String [] list = services.split("-");
        for(String id:list){
            MainController mainController = mainControllerList.get(id);
            if(null == mainController) throw new ServiceNotFoundException(id);
            stringBuilder.append(id).append(":").append(mainController.getResult()).append("  ");
        }
        return stringBuilder.toString();
    }

}
