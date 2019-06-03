package com.bm.base;

import com.myjar.Stringutil;

public class Sql {
	private String sql;//sql主体
	private String orderbykey;//排序字段
	private Integer orderbytype;//0:升序，1：降序
	private Integer page;//页数
	private Integer rows;//条数

	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getOrderbykey() {
		return orderbykey;
	}
	public void setOrderbykey(String orderbykey) {
		this.orderbykey = orderbykey;
	}
	public Integer getOrderbytype() {
		return orderbytype;
	}
	public void setOrderbytype(Integer orderbytype) {
		this.orderbytype = orderbytype;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	/**
	 * 对表数据排序分页，用户join操作，增加数据库效率
	 * */
	public static String page_rows(String sql,Integer page,Integer rows,String orderbykey,Integer orderbytype){
		page = page==null?1:page;
		rows = rows==null?10:rows;
		if(!Stringutil.isBlank(orderbykey)){
			sql = sql + " order by "+orderbykey;
			if(orderbytype==1)
				sql = sql + " desc ";
		}
		sql = "select * from ("+sql +") where a1> "+((page-1)*rows)+" and a1 <= "+(page*rows);
		return sql;
		}
	@Override
	public String toString() {return"Sqlsql"+sql+"orderbykey"+orderbykey+"orderbytype"+orderbytype+"page"+page+"rows"+rows;
}
	
	
	
	
	
	
	


	
    
    
    
    
	
	

}
