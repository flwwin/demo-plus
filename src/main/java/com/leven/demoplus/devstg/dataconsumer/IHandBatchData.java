package com.leven.demoplus.devstg.dataconsumer;

import java.io.Closeable;
import java.util.List;

public interface IHandBatchData<T> extends Closeable {

    void handData(T data);

    void handMultiData(List<T> datas);

    void submit(T data) throws InterruptedException;
}
