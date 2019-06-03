package com.bm.base.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Auth{
	/**
	 * 自定义权限控制系统，凡是有注解的就必须验证登录
	 * 
	 * */
	
//	/**是否可以导入店铺excel*/
//	public boolean shopExcel() default false;
	
	
	/**验证管理员权限*/
	public boolean admin() default false;
	
	/**店铺模块相关权限*/
	public Administration[] Shop() default {};
	
	/**导入店铺与审核权限*/
	public Administration[] Shopback() default {};
	
	/**商品权限*/
	public Administration[] Commodity() default {};
	
	
	/**店铺账单权限*/
	public Administration[] consumptionShop() default {};
	
	/**导入商品*/
	public Administration[] commodityback() default {};
	/**退货*/
	public Administration[] Returngoods() default {};
	/**活动相关*/
	public Administration[] Promotion() default {};
	/**库存相关*/
	public Administration[] stock() default {};
	/**优惠券相关*/
	public Administration[] coupon() default {};
	/**是否可以发布消息*/
	public boolean SystemMessage() default false;
	/**是否可以查询销售阈值,和导出报表*/
	public boolean Orderrelevance() default false;
	/***是否可以导出结算报表*/
	public boolean export() default false;
	/***是否可以查询结算规则*/
	public boolean ORDERSRULE() default false;
	/***是否可以操作订单相关资料*/
	public boolean orders() default false;
	/**是否可以查看运费险，导出运费*/
	public boolean Express() default false;
	/**是否可以添加店员*/
	public boolean clerk() default false;
	
	public enum Administration{
		/**查询*/
		SELECT, 
		/**修改*/
		UPDATE,
		/**删除*/
		DELETE,
		/**删除*/
		ADD
		
	}
	


}
