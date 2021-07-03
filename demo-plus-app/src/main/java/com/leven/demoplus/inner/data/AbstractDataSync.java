
package com.leven.demoplus.inner.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractDataSync extends AbstractBatchDataSync<DataLine> implements IDataSync {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSync.class);

	public AbstractDataSync() {
		super();
	}

	@Override
	public void submit(DataLine dataLine) {
		if (!isDataSyncOpen()) {
			LOGGER.info("source is close, current line is ignore! line: {}", dataLine.getOriginalLine());
			
			return;
		}
		
		super.submit(dataLine);
	}
	
	/**
	 * 将数据重新提交到队列中，内部重试时调用此方法
	 * @param 
	 * @return
	 */
	@Override
	protected void innerSubmit(final DataLine dataLine) {
		if (!isDataSyncOpen()) {
			LOGGER.info("source is close, current line is ignore! line: {}", dataLine.getOriginalLine());
			
			return;
		}
		
		super.innerSubmit(dataLine);
	}
	
	/**
	 * 处理多条数据，默认为将多条数据拆分后挨个处理，如果需要合并操作则需要覆盖此方法.
	 * 注意该方法调用完成后statLineList会被清空，如要异步，需先copy
	 * @param 
	 * @return
	 */
	@Override
	public void handleMulti(List<DataLine> statLineList) {
		for (DataLine statLine : statLineList) {
			handleSingle(statLine);
		}
	}
	
	/**
	 * 当前渠道的数据同步是否打开，
	 * @param 
	 * @return
	 */
	protected abstract boolean isDataSyncOpen();
	
	/**
	 * 处理单条信息
	 * @param 
	 * @return
	 */
	protected abstract void handleSingle(DataLine statLine);
}

