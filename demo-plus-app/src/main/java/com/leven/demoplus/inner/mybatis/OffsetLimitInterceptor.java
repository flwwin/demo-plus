package com.leven.demoplus.inner.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * 
 * 物理分页插件 , 只支持MySQL
 * 
 * 直接在原始的SQL上面添加 limit 子句
 * 
 * @author 80122172
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }) })
public class OffsetLimitInterceptor implements Interceptor {
	private static int MAPPED_STATEMENT_INDEX = 0;
	private static int PARAMETER_INDEX = 1;
	private static int ROWBOUNDS_INDEX = 2;
	private static String LIMIT_CLAUSE = " limit %s,%s";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		final Object[] queryArgs = invocation.getArgs();
		if(!(queryArgs[ROWBOUNDS_INDEX] instanceof PaginationRowBounds)){
			return invocation.proceed(); 
		}
		
		final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		final Object parameter = queryArgs[PARAMETER_INDEX];
		final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];

		if (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET && rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT) {
			return invocation.proceed();
		}

		final BoundSql boundSql = ms.getBoundSql(parameter);
		
		// 分页SQL
		String sql = boundSql.getSql() + String.format(LIMIT_CLAUSE, rowBounds.getOffset(),rowBounds.getLimit());
		//分页BoundSql
		BoundSql paginationBoundSql = PaginationHelper.buildBoundSql(ms, boundSql, sql, boundSql.getParameterMappings(), parameter);
		
		//替换成新的 MappedStatement
		queryArgs[MAPPED_STATEMENT_INDEX] = PaginationHelper.buildMappedStatement(ms, new BoundSqlSqlSource(paginationBoundSql));
		
		// 已经在SQL层面进行了分页, 不再使用Mybatis的默认方式分页
		queryArgs[ROWBOUNDS_INDEX] = new RowBounds();
		
		return invocation.proceed();
	}
	
	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;
		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}
		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
