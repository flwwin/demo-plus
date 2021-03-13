package com.leven.demo.plus.design.chain;

import com.leven.demo.plus.design.chain.enity.RequestContext;
import com.leven.demo.plus.design.chain.enity.RespondContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Data
@Slf4j
public class FilterFactory {
  private List<IWorkFilter<RequestContext, RespondContext>> filterList;
  private Map<String, IWorkFilter<RequestContext, RespondContext>> filterMap = new HashMap<>();
  private LinkedFilterChain linkedFilterChain;

  //@Value("#{chain}")
  List<String> incloudChain = new ArrayList<>(); // 配置执行链

  // 可以有两种方法注入 1： @Autowired  2：构造器注入
  @Autowired(required = false)
  private List<IWorkFilter<RequestContext, RespondContext>> filters;

  public FilterFactory(List<IWorkFilter<RequestContext, RespondContext>> filters) {
        filterList = filters;
    }

  @PostConstruct
  public void init() {
    // 原始链集合 ，可以基于动态配置，对执行的链接灵活拔插
    for (IWorkFilter filter : filterList) {
      filterMap.put(filter.getClass().getSimpleName(), filter);
    }
    // 获取执行链
    updateChain();
  }

  private void updateChain() {
    LinkedFilterChain realChain = new LinkedFilterChain();
    List<IWorkFilter<RequestContext, RespondContext>> filters = new ArrayList<>();
    realChain.setFilterChains(filters);
    this.linkedFilterChain = realChain;
    if (CollectionUtils.isEmpty(incloudChain)) {
        filters.addAll(filterList);
    }
    for (String filterName : incloudChain) {
      IWorkFilter realFilter = filterMap.get(filterName);
      filters.add(realFilter);
    }

  }

  public LinkedFilterChain getInstance() {
      LinkedFilterChain filterChain = null;
      try{
          filterChain = linkedFilterChain.clone();
      }catch (Exception e){
        log.error("get linkedFilterChain instance got error" ,e);
      }
      return filterChain;
  }
}
