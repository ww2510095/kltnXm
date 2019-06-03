package com.bm.orders.orders;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

/**
 * 订单
 * */
public class Orders extends BaseEN{
	@Override
	public String toString() {return"Ordersid"+id+"ordernumber"+ordernumber+"payment"+payment+"paymenttype"+paymenttype+"postfee"+postfee+"status"+status+"addtime"+addtime+"updatetime"+updatetime+"paymenttime"+paymenttime+"consigntime"+consigntime+"endtime"+endtime+"closetime"+closetime+"shippingtype"+shippingtype+"memberid"+memberid+"myaddressid"+myaddressid+"buyermesege"+buyermesege+"buyernick"+buyernick+"buyerrate"+buyerrate+"shopname"+shopname+"shopid"+shopid+"systemtype"+systemtype+"autosystem"+autosystem;
}
	private Long id; //id
	private String ordernumber; //订单号  
	private BigDecimal payment; //金额
	private Integer paymenttype; //付款方式1在线支付2货到付款，3微信，4支付宝
	private BigDecimal postfee; //邮费
	private Integer status; //状态1未付款2已付款未发货3已发货4交易完成5交易关闭，6到货,7，出货,8商家已接单,9,商家拒单,10已退单，11，已退款
	private Long addtime; //创建时间
	private Long updatetime; //更新时间
	private Long paymenttime; //付款时间
	private Long consigntime; //发货时间
	private Long endtime; //交易完成时间
	private Long closetime; //交易关闭时间
	private Integer shippingtype; //物流方式1自提2送货3无需物流（虚拟物品）
	private Long memberid; //用户id
	private Long myaddressid; //收货地址
	private String buyermesege; //买家留言||退货原因
	private String buyernick; //买家昵称
	private String buyerrate; //是否评价,1是
	private String shopname; //所属店铺名字
	private Long shopid; //店铺id
	private String systemtype; //所属系统
	private Integer autosystem; //是否已退款1：已退款
	private String onephone; //所属供应商
	
	private String b; //出货人
	private String b1; //收货人
	
	private String trajectory;//订单轨迹，时间;状态||拒绝原因
	private String couponid;//优惠券id
	
	private String postfeenumber;//供应商发货运单号
	
	
	private Long memberidsu;//订单绑定的人
	private Integer identity;//订单绑定的人的身份,1:导购，2线上店主，3，经销售
	
	


	public Integer getIdentity() {
		return identity;
	}
	public void setIdentity(Integer identity) {
		this.identity = identity;
	}
	public Long getMemberidsu() {
		return memberidsu;
	}
	public void setMemberidsu(Long memberidsu) {
		this.memberidsu = memberidsu;
	}
	public String getPostfeenumber() {
		return postfeenumber;
	}
	public void setPostfeenumber(String postfeenumber) {
		this.postfeenumber = postfeenumber;
	}
	public String getCouponid() {
		return couponid;
	}
	public void setCouponid(String couponid) {
		this.couponid = couponid;
	}
	public String getOnephone() {
		return onephone;
	}
	public void setOnephone(String onephone) {
		this.onephone = onephone;
	}
	public String getTrajectory() {
		return trajectory;
	}
	public void setTrajectory(String trajectory) {
		this.trajectory = trajectory;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public String getB1() {
		return b1;
	}
	public void setB1(String b1) {
		this.b1 = b1;
	}
	public Long getMyaddressid() {
		return myaddressid;
	}
	public void setMyaddressid(Long myaddressid) {
		this.myaddressid = myaddressid;
	}
	public Integer getAutosystem() {
		return autosystem;
	}
	public void setAutosystem(Integer autosystem) {
		this.autosystem = autosystem;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	public Integer getPaymenttype() {
		return paymenttype;
	}
	public void setPaymenttype(Integer paymenttype) {
		this.paymenttype = paymenttype;
	}
	public BigDecimal getPostfee() {
		return postfee;
	}
	public void setPostfee(BigDecimal postfee) {
		this.postfee = postfee;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getAddtime() {
		return addtime;
	}
	public void setAddtime(Long addtime) {
		this.addtime = addtime;
	}
	public Long getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Long updatetime) {
		this.updatetime = updatetime;
	}
	public Long getPaymenttime() {
		return paymenttime;
	}
	public void setPaymenttime(Long paymenttime) {
		this.paymenttime = paymenttime;
	}
	public Long getConsigntime() {
		return consigntime;
	}
	public void setConsigntime(Long consigntime) {
		this.consigntime = consigntime;
	}
	public Long getEndtime() {
		return endtime;
	}
	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}
	public Long getClosetime() {
		return closetime;
	}
	public void setClosetime(Long closetime) {
		this.closetime = closetime;
	}
	public Integer getShippingtype() {
		return shippingtype;
	}
	public void setShippingtype(Integer shippingtype) {
		this.shippingtype = shippingtype;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getBuyermesege() {
		return buyermesege;
	}
	public void setBuyermesege(String buyermesege) {
		this.buyermesege = buyermesege;
	}
	public String getBuyernick() {
		return buyernick;
	}
	public void setBuyernick(String buyernick) {
		this.buyernick = buyernick;
	}
	public String getBuyerrate() {
		return buyerrate;
	}
	public void setBuyerrate(String buyerrate) {
		this.buyerrate = buyerrate;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public Long getShopid() {
		return shopid;
	}
	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}
	public String getSystemtype() {
		return systemtype;
	}
	public void setSystemtype(String systemtype) {
		this.systemtype = systemtype;
	}
	


}


