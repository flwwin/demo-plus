package com.leven.demoplus.Design.observe;

import java.util.Map;

public interface Observer {
    void execute(Map<String, Object> paras);
    boolean isAsyn();
}
