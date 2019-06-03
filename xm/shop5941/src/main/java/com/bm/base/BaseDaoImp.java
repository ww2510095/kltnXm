package com.bm.base;

import com.Shop5941Application;
import com.myjar.Stringutil;

public class BaseDaoImp {
	
	/**
	 * 执行sql
	 * */
	   public String execSQL(Sql msql){
		   String sql =msql.getSql();
		   Shop5941Application.out("============================execSQL===========================");
		   Shop5941Application.out("sql:"+sql);
	        return sql;
	   }
	   /**
	    * 执行一条查询的sql
	    * */
	   public String exeSelectSql(Sql msql){
		   String sql =msql.getSql();
		   if(!Stringutil.isBlank(msql.getOrderbykey())){
			   sql=sql+" order by "+msql.getOrderbykey();
			   if(1==msql.getOrderbytype())
				   sql = sql+ " desc";
		   }
		   /**
		    * 分页
		    * */
		   if(msql.getPage()!=null||msql.getRows()!=null){
			   int page = msql.getPage()==null?1:msql.getPage();
			   int rows = msql.getRows()==null?10:msql.getRows();
			   
			    sql ="select * from (select rownum rownum1A,a.* from ("+sql +") a) where rownum1A> "+((page-1)*rows)+" and rownum1A <= "+(page*rows);
		   }

		   Shop5941Application.out("============================exeSelectSql===========================");
		   Shop5941Application.out("sql:"+sql);
		   return sql;
	   }
	   
	   /**
	    * 根据id删除数据
	    * */
	   public String deleteByid(String tabname,String id){
		   Shop5941Application.out("============================deleteByid===========================");
		   Shop5941Application.out("sql:"+"delete "+tabname+" where id ='"+id+"'");
	        return "delete "+tabname+" where id ='"+id+"'";
	   }
	   /**
	    * 根据id删除数据
	    * */
	   public String getByparameter(String tabname,String parametername, String parametervalue){
		   Shop5941Application.out("============================getByparameter===========================");
		   Shop5941Application.out("select * from (select * from "+tabname+" where "+parametername+" ='"+parametervalue+"' order by id desc) where  rownum=1");
		   return "select * from (select * from "+tabname+" where "+parametername+" ='"+parametervalue+"' order by id desc) where  rownum=1";
	   }
	   /**
	    * 根据id查询数据
	    * */
	   public String getById(String tabname,String id){
		   Shop5941Application.out("============================getById===========================");
		   Shop5941Application.out("sql:"+"select * from "+tabname+" where id = '"+id+"'");
		   return "select * from "+tabname+" where id = '"+id+"'";
	   }
	   /**
	    * 查询最大parametername+1
	    * */
	   public String getMaxParameter(String tabname, String parametername){
		   Shop5941Application.out("============================getMaxParameter===========================");
		   Shop5941Application.out("sql:"+"select nvl(max("+parametername+"),0)+1 from "+tabname);
		   return "select nvl(max("+parametername+"),0)+1 from "+tabname;
	   }
	   /**
	    * 根据指定非空字段查询数据
	    * */
	   public String getALL(Sql msql){
		   Integer page = msql.getPage();
		   Integer rows = msql.getRows();
		   if(page==null)page=1;
		   if(rows==null)rows=10;
		   String sql =msql.getSql();
		   if(!Stringutil.isBlank(msql.getOrderbykey())){
			   sql=sql+" order by "+msql.getOrderbykey();
			   if(1==msql.getOrderbytype())
				   sql = sql+ " desc";
		   }else{
			   sql = sql+ " order by id desc";
		   }
		   sql  = "select * from (select a.*,rownum rownum1A from ("+sql+")a )where rownum1A> "+((page-1)*rows)+" and rownum1A <= "+(page*rows);
		   Shop5941Application.out("============================getALL===========================");
		   Shop5941Application.out("sql:"+sql);
		   return sql;
	   }
	   
}
