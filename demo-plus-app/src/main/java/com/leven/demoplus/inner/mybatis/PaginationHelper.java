package com.leven.demoplus.inner.mybatis;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PaginationHelper {

	/**
	 * 计算总数
	 * 
	 * 如果未使用Mapper 动态代理 的方式
	 * 可以在自己写的Dao里面手动调用此方法,把total设置到Pagination里面去
	 * 
	 * @param sqlSession
	 * @param sqlId
	 * @param param
	 * @param paginator
	 * @return
	 */
	public static Long getTotalCount(SqlSession sqlSession,String sqlId,Object param,Paginator paginator){
		Long total = 0L;
		if(paginator != null && paginator.isContainTotal()){
			MappedStatement mappedStatement = sqlSession.getConfiguration().getMappedStatement(sqlId, false);
			final BoundSql boundSql = mappedStatement.getBoundSql(param);
			String countSql = "select count(1) from ("+boundSql.getSql()+") count_tmp";
			Connection connection = null;
			PreparedStatement countStmt = null;
			try {
				connection = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
				countStmt = connection.prepareStatement(countSql);
				
				setParameters(countStmt, sqlSession.getConfiguration(), boundSql, param, sqlId);
				
				ResultSet rs = countStmt.executeQuery();
				if(rs.next()) {
					total = rs.getLong(1);
				}
				
			} catch (SQLException e) {
				throw new BindingException(e);
			}
			finally {
				closeQuietly(countStmt);
				closeQuietly(connection);
			}
		}
		return total;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setParameters(PreparedStatement countStmt,Configuration configuration,BoundSql boundSql,Object parameterObject,String sqlId) throws SQLException{
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null) {
			return;
		}
        
        MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            if (parameterMapping.getMode() == ParameterMode.OUT) {
                continue;
            }
            
            Object value;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject == null) {
                value = null;
            } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                value = metaObject == null ? null : metaObject.getValue(propertyName);
            }
            TypeHandler typeHandler = parameterMapping.getTypeHandler();
            if (typeHandler == null) {
                throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + sqlId);
            }
            JdbcType jdbcType = parameterMapping.getJdbcType();
            if (value == null && jdbcType == null) {
				jdbcType = configuration.getJdbcTypeForNull();
			}
            
            typeHandler.setParameter(countStmt, i + 1, value, jdbcType);
        }
	}
	
	/**
	 * see: MapperBuilderAssistant
	 * 
	 * 根据 MappedStatement 和 新的SQL创建一个新的 MappedStatement
	 * @param ms
	 * @param newSqlSource
	 * @return
	 */
	public static MappedStatement buildMappedStatement(MappedStatement ms, SqlSource newSqlSource){
		
		Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuffer keyProperties = new StringBuffer();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}
	
	/**
	 * 创建一个新的BoundSql
	 * @param ms
	 * @param boundSql
	 * @param sql
	 * @param parameterMappings
	 * @param parameter
	 * @return
	 */
	public static  BoundSql buildBoundSql(MappedStatement ms, BoundSql boundSql, String sql,List<ParameterMapping> parameterMappings, Object parameter) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, parameter);
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}
	
	private static void closeQuietly(AutoCloseable closeable){
		try{
			if(closeable != null) {
				closeable.close();
			}
		}
		catch (Exception ignored) {
		}
	}
	
}
