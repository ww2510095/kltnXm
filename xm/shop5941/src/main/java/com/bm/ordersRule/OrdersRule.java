package com.bm.ordersRule;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class OrdersRule extends BaseEN{
	
	private Long id; //id
	private Long shoponeid; //代理商id
	private BigDecimal shopone; //代理商分成
	private BigDecimal shopto; //分公司分成
	private BigDecimal shop; //店铺分成
	private BigDecimal systemone; //系统分成
	private BigDecimal clerk; //推荐人分成（店员？二维码商家？）b2c
	private BigDecimal other; //其他分成（金币，这笔钱由系统出）c2c模式,此规则与上一条规则只能存在一条
	private Integer type; //1:供应商发货，2：门店直销
	
	
	
		public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
		@Override
	public String toString() {return"OrdersRuleid"+id+"shoponeid"+shoponeid+"shopone"+shopone+"shopto"+shopto+"shop"+shop+"systemone"+systemone+"clerk"+clerk+"other"+other;
		}
	public BigDecimal getOther() {
		return other;
	}
	public void setOther(BigDecimal other) {
		this.other = other;
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
	public Long getShoponeid() {
		return shoponeid;
	}
	public void setShoponeid(Long shoponeid) {
		this.shoponeid = shoponeid;
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
	

}
