package com.bm.ordersRule;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

/**
 * 经销商或者线上店主结算方案
 * */
public class Programme extends BaseEN{
	private Long id; //id
	private BigDecimal shopone; //代理商分成金额
	private BigDecimal shopto; //分公司分成金额
	private BigDecimal shop; //店铺分成金额
	private BigDecimal systemone; //系统分成金额
	private BigDecimal sales; //经销售
	private BigDecimal onlineshopkeeper; //线上店主
	
	
	
	@Override
	public String toString() {
		return "Programmeid" + id + "1shopone" + shopone + "1shopto" + shopto + "1shop" + shop + "1systemone"
				+ systemone + "1sales" + sales + "1onlineshopkeeper" + onlineshopkeeper;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

	
	

}
