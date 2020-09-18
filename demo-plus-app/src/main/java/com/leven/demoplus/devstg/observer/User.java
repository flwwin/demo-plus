package com.leven.demoplus.devstg.observer;

import lombok.Data;

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
