package com.bm.ordersRule.gd;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Gd_Commodity_key extends BaseEN{
	private Long id;
	private String youcode;//条码
	private BigDecimal shopone; //代理商分成
	private BigDecimal shopto; //分公司分成
	private BigDecimal shop; //店铺分成
	private BigDecimal systemone; //系统分成
	private BigDecimal clerk; //推荐人分成（店员？二维码商家？）b2c
	private BigDecimal jing_xiao_shang; //经销商分成
	private BigDecimal xian_shang_dian_zhu; //线上店主
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getYoucode() {
		return youcode;
	}
	public void setYoucode(String youcode) {
		this.youcode = youcode;
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
	public BigDecimal getClerk() {
		return clerk;
	}
	public void setClerk(BigDecimal clerk) {
		this.clerk = clerk;
	}
	
	public BigDecimal getJing_xiao_shang() {
		return jing_xiao_shang;
	}
	public void setJing_xiao_shang(BigDecimal jing_xiao_shang) {
		this.jing_xiao_shang = jing_xiao_shang;
	}
	public BigDecimal getXian_shang_dian_zhu() {
		return xian_shang_dian_zhu;
	}
	public void setXian_shang_dian_zhu(BigDecimal xian_shang_dian_zhu) {
		this.xian_shang_dian_zhu = xian_shang_dian_zhu;
	}
	@Override
	public String toString() {
		return "Gd_Commodity_keyid" + id + "1youcode" + youcode + "1shopone" + shopone + "1shopto" + shopto + "1shop"
				+ shop + "1systemone" + systemone + "1clerk" + clerk + "1jing_xiao_shang" + jing_xiao_shang
				+ "1xian_shang_dian_zhu" + xian_shang_dian_zhu;
	}

	

}
