
package com.leven.demoplus.inner.mybatis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRoutingDataSource.class);
    private static final String DB_HA_SELECTED = "db.ha.selected";
    private volatile ConfigHolder holder;
    private String identifier;
    private Map<Object, DataSource> resolvedDataSources;

    public DynamicRoutingDataSource() {
    }

    protected Object determineCurrentLookupKey() {
        Object lookupKey = this.getCurrentSetConf();
        return lookupKey;
    }

    private String getCurrentSetConf() {
        //Object configObj = KVStore.getValueFromAll("db.ha.selected");
        Object configObj = "123";
        if (null == configObj) {
            this.holder = null;
            return null;
        } else {
            String config = configObj.toString();
            if (StringUtils.isEmpty(config)) {
                this.holder = null;
                return null;
            } else if (null != this.holder && config.equals(this.holder.config)) {
                return this.holder.getConfigBySet(this.identifier);
            } else {
                ConfigHolder tmpHolder = new ConfigHolder(config);
                this.holder = tmpHolder;
                String key = tmpHolder.getConfigBySet(this.identifier);
                if (null != this.resolvedDataSources) {
                    DataSource ds = (DataSource)this.resolvedDataSources.get(key);
                    if (null == ds) {
                        LOGGER.info("cannot find datasource by key[{}] in {}", key, this.resolvedDataSources.keySet());
                    }
                }

                return key;
            }
        }
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.resolvedDataSources = this.resolvedDataSources();
    }

    public DataSource getTargetDataSourceBeforeRun() {
        return super.determineTargetDataSource();
    }

    private Map<Object, DataSource> resolvedDataSources() {
        try {
            Field f = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
            if (null == f) {
                return null;
            } else {
                f.setAccessible(true);
                return (Map)f.get(this);
            }
        } catch (SecurityException | IllegalAccessException | NoSuchFieldException var2) {
            LOGGER.error("get datasources error", var2);
            return null;
        }
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    private static class ConfigHolder {
        private String config;
        private Map<String, String> configMap;

        public ConfigHolder(String config) {
            this.config = config;
            this.parseConfig();
        }

        public String getConfigBySet(String key) {
            if (null != this.configMap && null != key) {
                //String currentSet = SetHelper.getCurrentSetName();
                String currentSet = "123";
                String value = null;
                if (StringUtils.isNotEmpty(currentSet)) {
                    value = (String)this.configMap.get(currentSet + "." + key);
                }

                if (null == value) {
                    value = (String)this.configMap.get(key);
                }

                return value;
            } else {
                return null;
            }
        }

        public void parseConfig() {
            if (!StringUtils.isEmpty(this.config)) {
                Map<String, String> tmpConfigMap = new HashMap();
                String[] configSecs = StringUtils.split(this.config, ',');
                int i = 0;

                for(int size = configSecs.length; i < size; ++i) {
                    String[] innerSecs = StringUtils.split(configSecs[i], ':');
                    if (innerSecs.length == 2) {
                        tmpConfigMap.put(innerSecs[0], innerSecs[1]);
                    }
                }

                this.configMap = tmpConfigMap;
            }
        }
    }
}
