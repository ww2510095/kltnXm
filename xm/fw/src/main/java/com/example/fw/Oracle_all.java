package com.example.fw;

public class Oracle_all {
	/**
	 * 类执行前缀
	 * create or replace and compile Java source named Oracle_all as 
	 * */

	/**主动
	 */
	public static String wangdian_sp(String tab_name) {
		/**
		 * orcale函数执行语句
		 * create or replace function wangdian_sp(tab_name varchar2) return varchar2 as
        language java name ' Oracle_all.wangdian_sp (java.lang.String) return java.lang.String ';

		 * */
		int i1 = tab_name.indexOf("_")+1;
		return tab_name.substring(i1,tab_name.indexOf("_",i1));
		
	}
	/**主动
	 */
	public static String sp_js(String juese) {
		/**
		 * orcale函数执行语句
		 * create or replace function sp_js(juese varchar2) return varchar2 as
        language java name ' Oracle_all.sp_js (java.lang.String) return java.lang.String ';

		 * */
		String aa ="'";
		String[] a = juese.split(";");
		for (String string : a) {
			aa=aa+string+"',";
		}
		if(a.length==0)return "";
		return aa.substring(0,aa.length()-1);
		
	}
	/**被动
	 */
	public static String wangdian_sp_b(String tab_name) {
		/**
		 * orcale函数执行语句
		 * create or replace function wangdian_sp_b(tab_name varchar2) return varchar2 as
        language java name ' Oracle_all.wangdian_sp_b (java.lang.String) return java.lang.String ';

		 * */
		int i1 = tab_name.indexOf("_")+1;
		i1=tab_name.indexOf("_",i1)+1;
		return tab_name.substring(i1,tab_name.length()-13);
		
	}

}
