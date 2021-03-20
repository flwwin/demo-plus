package com.leven.demoplus.kafka;

import java.io.Closeable;

public interface IHandBatchData<T> extends Closeable {

    void submit(T data) throws InterruptedException;
}
