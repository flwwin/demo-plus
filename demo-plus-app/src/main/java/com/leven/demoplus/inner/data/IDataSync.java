package com.leven.demoplus.inner.data;


import java.io.Closeable;

public interface IDataSync extends Closeable {
	void submit(DataLine dataLine);
}
