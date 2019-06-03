package com;

/**
 * 
 * 数据库临时脚本，用来计算一些临时的数据
 * */
public class Oa {
	/**
	create or replace and compile Java source named Oa as */
	
	public static Double abc(Integer a,Double b,Integer c,Integer d) {
		/**
		 * 
		
create or replace function abc(a number,b number,c number,d number) return number as
        language java name ' Oa.abc (java.lang.Integer,java.lang.Double,java.lang.Integer,java.lang.Integer) return java.lang.Double ';
		 * */
		if(d!=3){
			if(a==0)
				return b-c;
			else
				return b;
		}else{
			return b;
		}
		
	}
	public static String abd(String str) {
		String[] strs = str.split(";");
		if(strs[strs.length-1].equals("超过时限，系统自动签收")){
			strs[strs.length-1]="";
			strs[strs.length-2]="";
			if(strs[strs.length-3].equals("订单已发货")){
				strs[strs.length-4]="";
				strs[strs.length-3]="";
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String string : strs) {
			if(string.length()>1){
				sb.append(string);
				sb.append(";");
			}
			
			
		}
		return sb.toString();
		
	}

}
