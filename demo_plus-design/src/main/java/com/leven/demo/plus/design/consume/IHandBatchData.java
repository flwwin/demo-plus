package com.leven.demo.plus.design.consume;

import java.io.Closeable;

public interface IHandBatchData<T> extends Closeable {

    void submit(T data) throws InterruptedException;
}
