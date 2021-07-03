package com.leven.demoplus.inner.mybatis;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class MybatisLogInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MybatisLogInterceptor.class);

    @SuppressWarnings("unused")
    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        Object returnValue = null;
        long start = System.currentTimeMillis();
        returnValue = invocation.proceed();
        String dataSourceId = getDataSourceId(configuration);
        try {
            long end = System.currentTimeMillis();
            long time = (end - start);
            String sql = getSql(configuration, boundSql, sqlId, time);
            LOG.info("{} {}", dataSourceId, sql);
        } catch (Exception e) {
            LOG.error("{} {}", dataSourceId, sqlId, e);
        }
        return returnValue;
    }

    private String getDataSourceId(Configuration configuration) {
        try {
            DataSource ds = configuration.getEnvironment().getDataSource();
            if (ds instanceof DynamicRoutingDataSource) {
                ds = ((DynamicRoutingDataSource)ds).getTargetDataSourceBeforeRun();
                // just for compatible
            }else if(ds instanceof DynamicRoutingDataSource){
                ds = ((DynamicRoutingDataSource)ds).getTargetDataSourceBeforeRun();
            }

            String url = BeanUtils.getProperty(ds, "url");
            int startIndex = url.indexOf("://") + 3;
            int endIndex = url.indexOf("/", startIndex);

            return url.substring(startIndex, endIndex);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId, long time) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append("======[");
        str.append(sql);
        str.append("]====== cost:");
        str.append(time);
        str.append("ms");
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (!parameterMappings.isEmpty() && parameterObject != null) {
            Object[] paramArr = null;
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                paramArr = new String[1];
                paramArr[0] = getParameterValue(parameterObject);
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                int size = parameterMappings.size();
                paramArr = new String[size];
                for (int i = 0; i < size; i++) {
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    String propertyName = parameterMapping.getProperty();
                    String propVal = "";
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        propVal = getParameterValue(obj);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        propVal = getParameterValue(obj);
                    }
                    paramArr[i] = propVal;
                }
            }
            sql = replace(sql, "?", paramArr);
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * 使用方法
     *
     * StringUtils.replace("this ? the text ? ? replace","?","is","to","be")
     *
     * @param text		待替换的字符串
     * @param repl		替换的字符/表达式
     * @param with		替换成的字符串 ,
     * @return			替换后的字符串
     */
    public static String replace(String text,String repl,Object... with){

        if (org.apache.commons.lang3.StringUtils.isEmpty(text) || org.apache.commons.lang3.StringUtils.isEmpty(repl) || with == null || with.length == 0) {
            return text;
        }

        if(!text.contains(repl)){
            return text;
        }

        //都不为空则分 开处理
        StringBuilder resultText = new StringBuilder();

        String[] splitText = org.apache.commons.lang3.StringUtils.split(text, repl);

        // 处理结尾时要替换的字符串的时候会丢失的情况
        if(text.endsWith(repl)){
            splitText = ArrayUtils.addAll(splitText, org.apache.commons.lang3.StringUtils.EMPTY);
        }

        for(int i=0;i<splitText.length;i++){
            resultText.append(splitText[i]);
            if((i+1) != splitText.length){
                if(with.length > i){
                    resultText.append(with[i]);
                }
                else{
                    resultText.append(repl);
                }
            }
        }

        return resultText.toString();
    }
}
