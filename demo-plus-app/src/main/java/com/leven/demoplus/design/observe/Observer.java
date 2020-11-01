package com.leven.demoplus.design.observe;

import java.util.Map;

public interface Observer {
    void execute(Map<String, Object> paras);
    boolean isAsyn();
}
