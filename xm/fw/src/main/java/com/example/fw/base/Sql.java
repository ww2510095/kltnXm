package com.example.fw.base;

import com.myjar.Stringutil;

import lombok.Data;

@Data
public class Sql {
	private String sql;//sql主体
	private String orderbykey;//排序字段
	private Integer orderbytype;//0:升序，1：降序
	private Integer page;//页数
	private Integer rows;//条数

	
	
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
	

}
