package com.leven.demoplus.inner.sentry.stat;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CustomizedMonitorItem {
    public static final char INDICATOR_SPLIT_CHAR = '_';
    public static HashMap convert2Item(CustomizedStat stat, String host) {
        HashMap monitorItem = new HashMap();

        monitorItem.put("target", stat.getIdentifier());
        monitorItem.put("recordTime", System.currentTimeMillis());
        monitorItem.put("host", host);

        ConcurrentHashMap<String, CustomizedStatIndicator> indicatorMap = stat.getIndicatorMap();
        for(CustomizedStatIndicator indicator:indicatorMap.values())
        {
            if(indicator.hasStatInfo()) {
                CustomizedStatIndicator tmp = indicator.getAndReset();
                monitorItem.put(tmp.getIndicatorName() + INDICATOR_SPLIT_CHAR + CustomizedStatIndicator.STAT_SUM, tmp.getSum());
                monitorItem.put(tmp.getIndicatorName() + INDICATOR_SPLIT_CHAR + CustomizedStatIndicator.STAT_COUNT, tmp.getCnt());
                monitorItem.put(tmp.getIndicatorName() + INDICATOR_SPLIT_CHAR + CustomizedStatIndicator.STAT_AVG, tmp.getAvg());
                monitorItem.put(tmp.getIndicatorName() + INDICATOR_SPLIT_CHAR + CustomizedStatIndicator.STAT_MAX, tmp.getMax());
                monitorItem.put(tmp.getIndicatorName() + INDICATOR_SPLIT_CHAR + CustomizedStatIndicator.STAT_MIN, tmp.getMin());
            }
        }
        return monitorItem;
    }
}
