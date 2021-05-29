package com.leven.demoplus.javase.spi;

public class Logback implements ILog{
    @Override
    public void log() {
    System.out.println("logback .....");
    }
}
