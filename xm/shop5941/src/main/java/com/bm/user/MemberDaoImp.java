package com.bm.user;

import com.Shop5941Application;

public class MemberDaoImp {
	
	   public String getAuths(Long memberid){
		   String sql ="select DISTINCT authskey from "
		   		+ "Groupby where organization in"
		   		+ "(select groupbyname from Organization where memberid="+memberid+")";
		   Shop5941Application.out("============================getAuths===========================");
		   Shop5941Application.out("sql:"+sql);
	        return sql;
	   }
	   public String getAuthsAll(Long memberid){
		   String sql ="select DISTINCT * from "
		   		+ "Groupby where organization in"
		   		+ "(select groupbyname from Organization where memberid="+memberid+")";
		   Shop5941Application.out("============================getAuthsAll===========================");
		   Shop5941Application.out("sql:"+sql);
	        return sql;
	   }
	
	

}
