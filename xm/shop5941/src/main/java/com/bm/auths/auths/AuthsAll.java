package com.bm.auths.auths;

public class AuthsAll {
	/**
	 * 详细权限
	 * */
	
	
	//是否可以导入店铺excel
	public static final String SHOPEXCEL="SHOPEXCEL";
		
	//店铺模块相关权限
	public static final String SHOP_SELECT="SHOP_SELECT";
	public static final String SHOP_UPDATE="SHOP_UPDATE";
	public static final String SHOP_DELETE="SHOP_DELETE";
	public static final String SHOP_ADD="SHOP_ADD";
	//优惠券相关权限
	public static final String COUPON_SELECT="COUPON_SELECT";
	public static final String COUPON_UPDATE="COUPON_UPDATE";
	public static final String COUPON_DELETE="COUPON_DELETE";
	public static final String COUPON_ADD="COUPON_ADD";
	
	//导入店铺与审核权限
	public static final String SHOPBACK_SELECT="SHOPBACK_SELECT";
	public static final String SHOPBACK_UPDATE="SHOPBACK_UPDATE";
	public static final String SHOPBACK_DELETE="SHOPBACK_DELETE";
	public static final String SHOPBACK_ADD="SHOPBACK_ADD";
	
	//商品权限
	public static final String COMMODITY_SELECT="COMMODITY_SELECT";
	public static final String COMMODITY_UPDATE="COMMODITY_UPDATE";
	public static final String COMMODITY_DELETE="COMMODITY_DELETE";
	public static final String COMMODITY_ADD="COMMODITY_ADD";
	
	//商品导入权限
	public static final String COMMODITYBACK_SELECT="COMMODITYBACK_SELECT";
	public static final String COMMODITYBACK_UPDATE="COMMODITYBACK_UPDATE";
	public static final String COMMODITYBACK_DELETE="COMMODITYBACK_DELETE";
	public static final String COMMODITYBACK_ADD="COMMODITYBACK_ADD";
	
	//店铺账单权限
	public static final String CONSUMPTIONSHOP_SELECT="CONSUMPTIONSHOP_SELECT";
	public static final String CONSUMPTIONSHOP_UPDATE="CONSUMPTIONSHOP_UPDATE";
	public static final String CONSUMPTIONSHOP_DELETE="CONSUMPTIONSHOP_DELETE";
	public static final String CONSUMPTIONSHOP_ADD="CONSUMPTIONSHOP_ADD";
	//退货
	public static final String RETURNGOODS_SELECT="RETURNGOODS_SELECT";
	public static final String RETURNGOODS_UPDATE="RETURNGOODS_UPDATE";
	public static final String RETURNGOODS_DELETE="RETURNGOODS_DELETE";
	public static final String RETURNGOODS_ADD="RETURNGOODS_ADD";
	//活动相关
	public static final String PROMOTION_SELECT="PROMOTION_SELECT";
	public static final String PROMOTION_UPDATE="PROMOTION_UPDATE";
	public static final String PROMOTION_DELETE="PROMOTION_DELETE";
	public static final String PROMOTION_ADD="PROMOTION_ADD";
	//库存相关
	public static final String STOCK_SELECT="STOCK_SELECT";
	public static final String STOCK_UPDATE="STOCK_UPDATE";
	public static final String STOCK_DELETE="STOCK_DELETE";
	public static final String STOCK_ADD="STOCK_ADD";
	
	//是否可以发布消息
	public static final String SYSTEMMESSAGE="SYSTEMMESSAGE";
	//是否可以操作赔付
	public static final String EXPRESS="EXPRESS";
	//是否可以添加店员
	public static final String CLERK="CLERK";
	//是否可以查询销售阈值
	public static final String ORDERRELEVANCE="ORDERRELEVANCE";
	//是否可以操作订单相关
	public static final String ORDERS="ORDERS";
	//是否可以导出报表
	public static final String EXPORT="EXPORT";
	//是否可以查询结算规则
	public static final String ORDERSRULE="ORDERSRULE";
	

}
