package com;

/**
 * 数据库底层扩展
 */
public class OrcaleALL {
	
	/**
	 * 类执行前缀
	 * create or replace and compile Java source named hello as 
	 * */


	/**抽奖小数优先
	 */
	public static Long my_cj_cs(Long id,String column,Integer d) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_cj_cs(id number,column vchar2,d number) return number as
  			language java name ' OrcaleALL.my_cj_cs (java.lang.Long,java.lang.String,java.lang.Integer) return java.lang.Long ';
		 * */
		
		if(column.substring(0, 1).equals("0")){
			int a = Integer.parseInt(column.toString().substring(2));
			if(d%a==0)
				return id;
		}
		
		return null;
	}
	/**
	 * 类型赛选
	 */
	public static Integer my_type(String column,String str) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_type(column VARCHAR2,str VARCHAR2) return number as
  			language java name ' OrcaleALL.my_type (java.lang.String,java.lang.String) return java.lang.Integer ';
		 * */
		if(column==null||column.trim().equals(""))
			return 0;
		if(str==null||str.trim().equals(""))
			return 0;
		String[] strs = column.split(";");
		for (String string : strs) {
			if(string.equals(column))return 1;
		}
		
		return 0;
	}
	public static Integer my_imagesize(String column) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_imagesize(column VARCHAR2) return number as
  			language java name ' OrcaleALL.my_imagesize (java.lang.String) return java.lang.Integer ';
		 * */
		
		return column.split(";").length;
	}
	/**
	 * 此函数将字符串转化为数值，忽略非数字的字符
	 */
	public static Integer my_to_number(String column) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_to_number(column VARCHAR2) return number as
  			language java name ' OrcaleALL.my_to_number (java.lang.String) return java.lang.Integer ';
		 * */
		String str = "0";
		if (column != null && !"".equals(column)) {
			for (int i = 0; i < column.length(); i++) {
				if (column.charAt(i) >= 48 && column.charAt(i) <= 57) {
					str += column.charAt(i);
				}
			}
			
		}else{
			return 0;
		}
		return Integer.valueOf(str);
	}
	/**
	 * 优惠价扩展，次函数返回优惠价名字值
	 */
	public static String my_to_CouponName(Long column) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_to_CouponName(column number) return VARCHAR2 as
  			language java name ' OrcaleALL.my_to_CouponName (java.lang.Long) return java.lang.String ';
		 * */
	if(column==null)
		return "积分兑换";
	if(column.toString().equals("123"))
			return "邮费补偿(1)";
	if(column.toString().equals("-1"))
		return "成为分销商自动发放(1)";
	if(column.toString().equals("-2"))
		return "成线上店主自动发放(1)";
	
		return "注册自动发放(1)";
	}
	
	
	/**
	 *比较参数a和b是否相同，如果相同返回a+b，否则返回指定参数
	 */
	public static Double my_to_so(Integer i,Long a,Long b,Double d1,Double d2) {
		/**
		 * orcale函数执行语句
		 * create or replace function my_to_so(column number,column1 number,column2 number,column3 number,column5 number) return number as
  			language java name 
  			' OrcaleALL.my_to_so (java.lang.Integer,java.lang.Long,java.lang.Long,java.lang.Double,java.lang.Double) return java.lang.Double ';
		 * */
		if(a==null)return d1;
		if(b==null)return d2;
		if(a.toString().equals(b.toString()))return d1+d2;
		else
		{
			if(i==1)
				return d1;
			else
				return d2;
		}
		
	}
	/**
	 * 自定义的if
	 * */
	public static String my_if(Integer A,Integer B,String isTrue,String isFalse){
		/**
		 * orcale函数执行语句
		 * create or replace function my_if(a number,b number, isTrue VARCHAR2,isFalse VARCHAR2) return VARCHAR2 as
  			language java name ' OrcaleALL.my_if (java.lang.Integer,java.lang.Integer,java.lang.String,java.lang.String) 
  			return java.lang.String ';
		 * */
		if(A>B) return isTrue;
		else return isFalse;
		
	}
	/**
	 * 邮费计算，
	 * d，金额，
	 * type，身份，1，店员，2店长，3，供应商
	 * */
	public static Integer getPostfee(Double d,Integer type){
		return 0;
		/**
		 * orcale函数执行语句
		 * create or replace function getPostfee(a number,b number) return number as
  			language java name ' OrcaleALL.getPostfee (java.lang.Double,java.lang.Integer) 
  			return java.lang.String ';
		 * */
		/**
		int key = 88;
		if(d>0){
			//正向业务
			if(type==1){
				if(d<key)
					return 0;
				else
					return 2;
			}
			if(type==2){
				if(d<key)
					return 0;
				else
					return 5;
			}else{
					return -7;
			}
			
		}else{
			//逆向业务
			if(type==1){
				if(d<key)
					return 0;
				else
					return -2;
			}
			if(type==2){
				if(d<key)
					return 0;
				else
					return -5;
			}else{
					return 7;
			}
		}
		*/
	}
	
	
}
