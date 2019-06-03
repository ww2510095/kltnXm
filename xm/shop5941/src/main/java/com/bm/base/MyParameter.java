package com.bm.base;

import java.math.BigDecimal;

public class MyParameter {
	
	public static final String Home_name="ZSACK6GQJP55CWX";
	
	
	
	public static final Long w_out_time=60L*30L*1000L;//毫秒
	
	/**服务器是否对外提供服务**/
	public static boolean SERVICE_TURE=true;
	/**袜子商品编号*/
	public static final String clsid="1543997968786"; 
	/**袜子一组数量*/
	public static final int clssize=50; 
	/**注册默认推荐人*/
	public static String memberaddrecommend="17790271590";
	
	/**守护系统版本号*/
	public static final int system = 1;
	
	/**心跳服务器所在ip地址*/
	public static final String TASK_ADDRESS="123.207.147.134:8090";
//	public static final String TASK_ADDRESS="192.168.1.254:8090";
	
	/**
	 * 单品减多少钱
	 * */
	public static final int ORDERONESUB=9;
	/**
	 * 拼单加多少钱
	 * */
	public static final int ORDERONEADD=4;

	/**短信状态*/
	public static final int phone_message_type0=0;//注册
	public static final int phone_message_type1=1;//找回密码
	public static final int phone_message_type2=2;//修改绑定手机号
	public static final int phone_message_type3=3;//商家发货短信提醒
	public static final int phone_message_type4=4;//绑定支付宝
	
	public static final Long  TASK_ORDERS9=1000L*60L*60L*24L*12L;//商家拒单时间|毫秒
	
	
	public static final Long  ReturnOrdersTime=1000L*60L*60L*24*7L;//可退货时间|门店自动发货时间|毫秒
	
	public static final Long  OrdersTrueTime=1000L*60L*60L*24*7L;//用户自动收货时间|毫秒
	
	public static final Long  OrdersjdTime=1000L*60L*30L;//自动接单时间|毫秒
	/**
	 * 云片网apikey
	 * */
	public static final String MESSAGE_APIKEY="258894d07831bb0902521a0236b61066";
	/**短信url*/
	public static final String MESSAGE_URL="https://sms.yunpian.com/v2/sms/single_send.json";
	/**
	 * 秘钥
	 * */
	public static final String  KEY_MEMBER="1qaz2wsxadmin";//用户验证
	public static final String  KEY_SHOP="1qaz2wsxadmin";//店铺
	public static final String  KEY_CONSUMPTION="1qaz2wsxadmin";//消费秘钥
	public static final String  KEY_ORDERS="6zx54f9s5";//订单
	public static final String  KEY_TASK="456646fndjksafs";//心跳系统秘钥
	
	/** Tomcat虚拟盘符 */
	public static final String TomcatSD = "C:/";
	/** Tomcat虚拟目录 */
	public static final String Tomcat = TomcatSD+"upload/";//正常文件路径
//	public static final String Tomcat = TomcatSD+"debug/";//debug文件路径
	/** 图片虚拟目录 */
	public static final String TomcatFileImage = Tomcat+"images/";
	/** 文件队列，非图片的文件放到另外一个文件夹 */
	public static final String TomcatFile = Tomcat+"file/";
	/** 系统电话，穿插数字保证和表名没有交集 */
	public static final String SYSTEM_PHONE = "S1Y2S3T4E5M6P7H8O9N0E";
	
	
	/**
	 * 配置文件
	 * */
	public static final String  System=TomcatSD+"/config/";//配置文件路径
	public static final String  System1=System+"1.txt";//注册协议
	public static final String  System2=System+"2.txt";//隐私协议
	public static final String  Phone=System+"systemphone.txt";//隐私协议
	public static final String  Distributor=System+"Distributor.txt";//经销商协议
	public static final String  shopkeeper=System+"shopkeeper.txt";//线上店主协议
	public static final String  WEI_PRCK=System+"apiclient_cert.p12";//微信证书
	
	public static final BigDecimal mBigDecimal_0 = new BigDecimal("0");
	
	/** 短信超时时间 ,秒*/
	public static final int memssage_overtime = 60;
	
	
	
	
	/** redis缓存用户前缀,不同类别采用不同前缀，保证key不会因为混乱而覆盖*/
	public enum Redisinfo{
		redis_member_login,//登录信息login_
		redis_member_user,//用户资料信息user_
		getall,//查询getall_
		exeSelectSql,//查询exeSelectSql_
		getbyid,//查询getbyid_
		getByparameter,//查询getByparameter_
	}
	public static final String RETURN_LOGIN="重新登陆\n\t点击我的➡设置➡退出登陆";

	
	
	/**模块是否存在*/
	public static   boolean  shop_true=false;//商店模块，如果存在即为多店铺，不存在为单商铺模式
	public static   boolean  consumption_true=false;//钱包模块
	public static  boolean  commodity_true=false;//商品模块
	public static  boolean  file_true=false;//文件模块
	public static  boolean  auths_true=false;//权限模块
	public static  boolean  order_true=false;//订单系统
	public static  boolean  friends_true=false;//好友系统
	public static  boolean  clerk_true=false;//店员系统
	public static  boolean  ordersRule_true=false;//分成规则
	public static  boolean  ShoppingCart_true=false;//购物车
	public static  boolean  stock_true=false;//库存
	public static  boolean  myaddress=false;//收货地址
	public static  boolean  evaluate_true=false;//评价
	public static  boolean  promotion_true=false;//促销
	public static  boolean  returngoods=false;//退货
	public static  boolean  advertisement=false;//广告
//	public static  boolean  scan=false;//
	public static  boolean  search=false;//热门搜索
	public static  boolean  feedback=false;//一键反馈
	public static  boolean  systemMessage=false;//消息
	public static  boolean  express=false;//运费险
	public static  boolean  systemexpress=false;//赔付明细
	public static  boolean  postfees=false;//运费险---已废弃
	public static  boolean  freeshipping=false;//包邮规则
	public static  boolean  coupon=false;//优惠券则
	public static  boolean  Version=false;//版本号
	public static  boolean  putforward=false;//提现
	public static  boolean  identity=false;//导购角色相关
	public static  boolean  zsh=false;//招商会
	public static  boolean  Commission=false;//结算相关
	public static  boolean  help=false;//帮助
	public static  boolean  ms=false;//秒杀
	public static boolean ys=false;//预售
	public static boolean pdd=false;
	public static boolean gd=false;
	public static boolean envelopes=false;
	public static boolean goldcoin=false;
	public static boolean Goldcoincoupon=false;

	public static boolean erp=false;

	public static boolean xuankuan=false;

	public static boolean cj=false;
	public static boolean gfakchd=false;








	public static boolean orderscard=false;
	
	
	

}
