package com.leven.demoplus.inner.mybatis;

import java.io.Serializable;

/**
 * 
 * 分页参数
 * 
 * Myabtis RowBounds 没有实现序列化
 * 
 * @author 80122172
 */
public class Paginator implements Serializable{

	private static final long serialVersionUID = -1059458845709904898L;
	
	private static final int DEFAULT_LIMIT = 10;
	
	private static final int DEFAULT_OFFSET = 0;
	
	private static final int DEFAULT_PAGE = 1;
	
	/**
	 * 起始偏移
	 */
	private int offset;
	
	/**
	 * 一页数量
	 */
	private int limit;
	
	/**
	 * 是否请求总数
	 */
	private boolean containTotal;
	
	public Paginator(){
	}
	
	public Paginator(int offset,int limit){
		this.offset = offset;
		this.limit = limit;
		checkOffset(offset);
		checkLimit(limit);
	}
	
	public Paginator(int offset,int limit,boolean containTotal){
		this.offset = offset;
		this.limit = limit;
		this.containTotal = containTotal;
		checkOffset(offset);
		checkLimit(limit);
	}
	
	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setOffset(int offset) {
		this.offset = offset;
		checkOffset(offset);
	}

	public void setLimit(int limit) {
		this.limit = limit;
		checkLimit(limit);
	}

	public boolean isContainTotal() {
		return containTotal;
	}

	public void setContainTotal(boolean containTotal) {
		this.containTotal = containTotal;
	}
	
	private void checkOffset(int offset){
		if(offset < DEFAULT_OFFSET) {
			this.limit = DEFAULT_OFFSET;
		}
	}
	
	private void checkLimit(int limit){
		if(limit <= 0) {
			this.limit = DEFAULT_LIMIT;
		}
	}
	
	public static Paginator createByPageAndPageCount(int page,int pageCount,boolean containTotal){
		
		if(page < DEFAULT_PAGE) {
			page = DEFAULT_PAGE;
		}
		
		if(pageCount <= 0 ) {
			pageCount = DEFAULT_LIMIT;
		}
		
		return new Paginator((page-1)*pageCount, pageCount, containTotal);
	}
}
