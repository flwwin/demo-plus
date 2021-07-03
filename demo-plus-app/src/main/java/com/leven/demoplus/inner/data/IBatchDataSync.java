package com.leven.demoplus.inner.data;

import java.io.Closeable;
import java.util.List;

/**
 * 批量数据处理
 */
public interface IBatchDataSync<T> extends Closeable {
	/**
	 * 提交单条数据
	 * @param 
	 * @return
	 */
	void submit(T data);
	
	/**
	 * 处理多条数据
	 * @param 
	 * @return
	 */
	void handleMulti(List<T> dataList);
}
