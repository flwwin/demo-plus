package com.leven.demoplus.Design.observe;

public abstract class AbstractObserver implements Observer {

    public boolean asyn = true;

    public boolean isAsyn() {
        return asyn;
    }

    public void setAsyn(boolean asyn) {
        this.asyn = asyn;
    }

}
