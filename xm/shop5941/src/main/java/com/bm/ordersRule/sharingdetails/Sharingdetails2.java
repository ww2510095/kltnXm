package com.bm.ordersRule.sharingdetails;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Sharingdetails2 extends BaseEN{
	private Long id; //id
	private Long ordersid; //订单id
	private BigDecimal shopone; //代理商分成金额
	private BigDecimal shopto; //分公司分成金额
	private BigDecimal shop; //店铺分成金额
	private BigDecimal systemone; //系统分成金额
	private BigDecimal sales; //经销售
	private BigDecimal onlineshopkeeper; //线上店主
	private Integer state; //状态：0未清算1，已清算，
	private Long memberid; //订单绑定的用户
	private Long memberid1; //购买用户
	private Long salesmemberid; //对应的经销售用户
	private Long onlineshopkeepermemberid; //对应的线上店主账户
	private Integer istrue; //金额是否已打入用户账户
	private Long fid; //方案id
	private Long shopid; //绑定的店铺id
	
	//--------非字段---------------
		private String phone; //订单绑定的用户
		private String phone1; //购买用户
		
		
		
		
		@Override
		public String toString() {
			return "Sharingdetails2id" + id + "1ordersid" + ordersid + "1shopone" + shopone + "1shopto" + shopto
					+ "1shop" + shop + "1systemone" + systemone + "1sales" + sales + "1onlineshopkeeper"
					+ onlineshopkeeper + "1state" + state + "1memberid" + memberid + "1memberid1" + memberid1
					+ "1salesmemberid" + salesmemberid + "1onlineshopkeepermemberid" + onlineshopkeepermemberid
					+ "1istrue" + istrue + "1fid" + fid + "1shopid" + shopid + "1phone" + phone + "1phone1" + phone1;
		}
		public Long getShopid() {
			return shopid;
		}
		public void setShopid(Long shopid) {
			this.shopid = shopid;
		}
		public Long getFid() {
			return fid;
		}
		public void setFid(Long fid) {
			this.fid = fid;
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
		public BigDecimal getSales() {
			return sales;
		}
		public void setSales(BigDecimal sales) {
			this.sales = sales;
		}
		public BigDecimal getOnlineshopkeeper() {
			return onlineshopkeeper;
		}
		public void setOnlineshopkeeper(BigDecimal onlineshopkeeper) {
			this.onlineshopkeeper = onlineshopkeeper;
		}
		public Integer getState() {
			return state;
		}
		public void setState(Integer state) {
			this.state = state;
		}
		public Long getMemberid() {
			return memberid;
		}
		public void setMemberid(Long memberid) {
			this.memberid = memberid;
		}
		public Long getMemberid1() {
			return memberid1;
		}
		public void setMemberid1(Long memberid1) {
			this.memberid1 = memberid1;
		}
		public Long getSalesmemberid() {
			return salesmemberid;
		}
		public void setSalesmemberid(Long salesmemberid) {
			this.salesmemberid = salesmemberid;
		}
		public Long getOnlineshopkeepermemberid() {
			return onlineshopkeepermemberid;
		}
		public void setOnlineshopkeepermemberid(Long onlineshopkeepermemberid) {
			this.onlineshopkeepermemberid = onlineshopkeepermemberid;
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
	
	

		

}
