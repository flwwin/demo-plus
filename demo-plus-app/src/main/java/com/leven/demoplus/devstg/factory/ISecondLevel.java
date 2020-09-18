package com.leven.demoplus.devstg.factory;

import org.springframework.stereotype.Component;

@Component
public class ISecondLevel extends AbstractHandData implements IHandData {
    @Override
    public void handData() {
        System.out.println("二级用户处理");
    }

    @Override
    public int getLevel() {
        return 2;
    }
}
