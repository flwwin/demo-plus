package com.lenven.demo.plus.common.queue;

import java.io.Closeable;

public interface IHandBatchData<T> extends Closeable {

    void submit(T data) throws InterruptedException;
}
