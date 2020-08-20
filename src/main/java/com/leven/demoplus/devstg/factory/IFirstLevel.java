package com.leven.demoplus.devstg.factory;

import org.springframework.stereotype.Component;

@Component
public class IFirstLevel extends AbstractHandData implements IHandData {
    @Override
    public void handData() {
        System.out.println("一级用户处理");
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
