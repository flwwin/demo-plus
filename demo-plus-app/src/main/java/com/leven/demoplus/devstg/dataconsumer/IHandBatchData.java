package com.leven.demoplus.devstg.dataconsumer;

import com.leven.demoplus.enity.DataLine;

import java.io.Closeable;
import java.util.List;

public interface IHandBatchData<T> extends Closeable {

    void submit(T data) throws InterruptedException;
}
