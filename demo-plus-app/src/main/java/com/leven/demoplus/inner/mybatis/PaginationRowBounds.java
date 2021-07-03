package com.leven.demoplus.inner.mybatis;

import org.apache.ibatis.session.RowBounds;

public class PaginationRowBounds extends RowBounds {

	public PaginationRowBounds(int offset,int limit){
		super(offset, limit);
	}
	
}
