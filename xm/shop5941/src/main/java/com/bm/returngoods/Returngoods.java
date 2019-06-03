package com.bm.returngoods;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Returngoods extends BaseEN{
	
	private Long id; //id
	private Long orderrelevanceid; //购买id
	private Long memberid; //处理人
	private Integer istrue; //是否同意0:未处理，-1，不同意，1已同意,2:退仓，3：仓库收货
	private String reason; //退货原因
	private BigDecimal price; //商品售价
	private BigDecimal refund; //退价
	private String refuse; //拒绝退货的原因
	private String shopid; //商铺
	private String logistics; //物流单号
	private Integer a; //退了多少件
	private Integer returnnum; //是否退款,1:已退款，0：未退款，空，其他数据
	private Long Systemexpress; //下一级审核人
	
	
	public Integer getReturnnum() {
		return returnnum;
	}
	public void setReturnnum(Integer returnnum) {
		this.returnnum = returnnum;
	}
	
	public Long getSystemexpress() {
		return Systemexpress;
	}
	public void setSystemexpress(Long systemexpress) {
		Systemexpress = systemexpress;
	}
	public Integer getA() {
		return a;
	}
	public void setA(Integer a) {
		this.a = a;
	}
	public String getLogistics() {
		return logistics;
	}
	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}
	@Override
	public String toString() {return"Returngoodsid"+id+"orderrelevanceid"+orderrelevanceid+"memberid"+memberid+"istrue"+istrue+"reason"+reason+"price"+price+"refund"+refund+"refuse"+refuse+"shopid"+shopid;
}
	public String getShopid() {
		return shopid;
	}
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderrelevanceid() {
		return orderrelevanceid;
	}
	public void setOrderrelevanceid(Long orderrelevanceid) {
		this.orderrelevanceid = orderrelevanceid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getRefund() {
		return refund;
	}
	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}
	public String getRefuse() {
		return refuse;
	}
	public void setRefuse(String refuse) {
		this.refuse = refuse;
	}
	
	


}
