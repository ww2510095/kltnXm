package com.bm.ordersRule.sharingdetails;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Sharingdetails extends BaseEN{
	private Long id; //id
	private Long ordersid; //订单id||购买id，如果价格是负数则为退货，购买的id
	private BigDecimal shopone; //代理商分成金额
	private BigDecimal shopto; //分公司分成金额
	private BigDecimal shop; //店铺分成金额
	private BigDecimal systemone; //系统分成金额
	private BigDecimal other; //其他分成（金币，这笔钱由系统出）c2c模式
	private BigDecimal clerk; //推荐人分成（店员？二维码商家？）
	private Integer state; //状态：0未清算1，已清算，
	private Long memberid; //订单绑定的用户
	private Long memberid1; //购买用户
	private Integer istrue; //金额是否已打入用户账户
	private Long sharingdetailsshopid; //订单绑定店铺
	
	//--------非字段---------------
		private String phone; //订单绑定的用户
		private String phone1; //购买用户
		
		
		
	
	

	public Long getSharingdetailsshopid() {
			return sharingdetailsshopid;
		}
		public void setSharingdetailsshopid(Long sharingdetailsshopid) {
			this.sharingdetailsshopid = sharingdetailsshopid;
		}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	


	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public Long getMemberid1() {
		return memberid1;
	}
	public void setMemberid1(Long memberid1) {
		this.memberid1 = memberid1;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	
	@Override
	public String toString() {
		return "Sharingdetailsid" + id + "1ordersid" + ordersid + "1shopone" + shopone + "1shopto" + shopto + "1shop"
				+ shop + "1systemone" + systemone + "1other" + other + "1clerk" + clerk + "1state" + state + "1memberid"
				+ memberid + "1memberid1" + memberid1 + "1istrue" + istrue + "1sharingdetailsshopid"
				+ sharingdetailsshopid + "1phone" + phone + "1phone1" + phone1;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public BigDecimal getClerk() {
		return clerk;
	}
	public void setClerk(BigDecimal clerk) {
		this.clerk = clerk;
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
	public BigDecimal getShopone() {
		return shopone;
	}
	public void setShopone(BigDecimal shopone) {
		this.shopone = shopone;
	}
	public BigDecimal getShopto() {
		return shopto;
	}
	public void setShopto(BigDecimal shopto) {
		this.shopto = shopto;
	}
	public BigDecimal getShop() {
		return shop;
	}
	public void setShop(BigDecimal shop) {
		this.shop = shop;
	}
	public BigDecimal getSystemone() {
		return systemone;
	}
	public void setSystemone(BigDecimal systemone) {
		this.systemone = systemone;
	}
	public BigDecimal getOther() {
		return other;
	}
	public void setOther(BigDecimal other) {
		this.other = other;
	}

}
