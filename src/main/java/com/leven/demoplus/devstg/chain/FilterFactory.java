package com.leven.demoplus.devstg.chain;

import com.leven.demoplus.devstg.chain.enity.RequextContext;
import com.leven.demoplus.devstg.chain.enity.RespondContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class FilterFactory {
    private List<AbstractFilter> filterList;
    private Map<String,AbstractFilter> filterMap = new HashMap<>();
    private LinkedFilterChain<RequextContext,RespondContext> linkedFilterChain;

    @Value("chain.incloud.chain")
    private List<String> incloudChain; //配置执行链

    //可以有两种方法注入 1： @Autowired  2：构造器注入
    @Autowired(required = false)
    private List<AbstractFilter> filters;

    public FilterFactory(List<AbstractFilter> filters) {
        filterList = filters;

    }

    @PostConstruct
    public void init(){
        //原始链集合
        for (AbstractFilter filter : filterList) {
            filterMap.put(filter.getClass().getSimpleName(),filter);
        }
        //获取执行链
        updateChain();
    }

    private void updateChain() {
        LinkedFilterChain<RequextContext, RespondContext> realChain = new LinkedFilterChain<>();
        List<IFilter<RequextContext,RespondContext>> filters = new ArrayList<>();
        realChain.setFilterChains(filters);
        for (String filterName : incloudChain) {
            AbstractFilter realFilter = filterMap.get(filterName);
            filters.add(realFilter);
        }
        this.linkedFilterChain=realChain;
    }

    public LinkedFilterChain<RequextContext,RespondContext> getInstance() throws CloneNotSupportedException {
        return linkedFilterChain.clone();
    }
}
