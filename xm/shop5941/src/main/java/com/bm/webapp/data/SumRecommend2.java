package com.bm.webapp.data;

import com.bm.base.BaseEN;

public class SumRecommend2 extends BaseEN {
	
	private String phone; 
	private String suma; //合计已结算金额
	private String sumb; //合计未结算金额
	private String clerka; //作为导购已结算金额
	private String clerkb; //作为导购未结算金额
	private String shopa="不是店铺"; //作为店铺已结算金额
	private String shopb="不是店铺"; //作为店铺未结算金额
	private String shoponea="不是供应商"; //作为供应商已结算金额
	private String shoponeb="不是供应商"; //作为供应商未结算金额
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSuma() {
		return suma;
	}
	public void setSuma(String suma) {
		this.suma = suma;
	}
	public String getSumb() {
		return sumb;
	}
	public void setSumb(String sumb) {
		this.sumb = sumb;
	}
	public String getClerka() {
		return clerka;
	}
	public void setClerka(String clerka) {
		this.clerka = clerka;
	}
	public String getClerkb() {
		return clerkb;
	}
	public void setClerkb(String clerkb) {
		this.clerkb = clerkb;
	}
	public String getShopa() {
		return shopa;
	}
	public void setShopa(String shopa) {
		this.shopa = shopa;
	}
	public String getShopb() {
		return shopb;
	}
	public void setShopb(String shopb) {
		this.shopb = shopb;
	}
	public String getShoponea() {
		return shoponea;
	}
	public void setShoponea(String shoponea) {
		this.shoponea = shoponea;
	}
	public String getShoponeb() {
		return shoponeb;
	}
	public void setShoponeb(String shoponeb) {
		this.shoponeb = shoponeb;
	}
	@Override
	public String toString() {
		return "SumRecommend2phone" + phone + "1suma" + suma + "1sumb" + sumb + "1clerka" + clerka + "1clerkb" + clerkb
				+ "1shopa" + shopa + "1shopb" + shopb + "1shoponea" + shoponea + "1shoponeb" + shoponeb;
	}
	
	

}
