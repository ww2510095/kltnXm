package com.bm.postfees;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Postfees extends BaseEN{
	private Long id; //id
	private Long ordersid; //订单id
	private Long oneid; //供应商id
	private BigDecimal postfee; //邮费多少
	private Integer indemnity; //赔付人，0：供应商，1：店铺和店员共同承担
	private Long shopid; //订单关联的店铺id
	private Long clerk; //购买人所绑定的店员
	private Integer istrue; //是否结算，0：未结算，1：已结算
	
	
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrdersid() {
		return ordersid;
	}
	public void setOrdersid(Long ordersid) {
		this.ordersid = ordersid;
	}
	public Long getOneid() {
		return oneid;
	}
	public void setOneid(Long oneid) {
		this.oneid = oneid;
	}
	public BigDecimal getPostfee() {
		return postfee;
	}
	public void setPostfee(BigDecimal postfee) {
		this.postfee = postfee;
	}
	public Integer getIndemnity() {
		return indemnity;
	}
	public void setIndemnity(Integer indemnity) {
		this.indemnity = indemnity;
	}
	public Long getShopid() {
		return shopid;
	}
	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}
	public Long getClerk() {
		return clerk;
	}
	public void setClerk(Long clerk) {
		this.clerk = clerk;
	}
	@Override
	public String toString() {
		return "Postfeesid" + id + "1ordersid" + ordersid + "1oneid" + oneid + "1postfee" + postfee + "1Indemnity"
				+ indemnity + "1shopid" + shopid + "1clerk" + clerk;
	}
	
	
	

}
