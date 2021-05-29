package com.leven.demoplus.javase.spi;

public class Log4j implements  ILog{
    @Override
    public void log() {
    System.out.println("log4j......");
    }
}
