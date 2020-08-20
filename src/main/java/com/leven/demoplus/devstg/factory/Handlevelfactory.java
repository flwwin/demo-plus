package com.leven.demoplus.devstg.factory;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Handlevelfactory {
    private Map<Integer,IHandData> levelMap;

    public Handlevelfactory(List<IHandData> datas) {
        this.levelMap = new HashMap();
        for (IHandData data : datas) {
            levelMap.put(data.getLevel(),data);
        }
    }

    public IHandData getInstance(int level){
        IHandData handData = levelMap.get(level);
        if (null == handData){
            return new DefaultLevel();
        }
        return handData;
    }
}
