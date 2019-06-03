package com.bm.webapp.data;

import com.bm.base.BaseEN;

public class ShopMember extends BaseEN{
	
	private String summember;//会有总数
	private String newsummember;//新增会有
	private String newsumclerk;//新增导购
	private String active;//活跃用户
	private String money;//成交金额
	private String code;//门店编号
	private String name;//门店名字
	private String province;//||经营片区
	
	
	
	
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSummember() {
		return summember;
	}
	public void setSummember(String summember) {
		this.summember = summember;
	}
	public String getNewsummember() {
		return newsummember;
	}
	public void setNewsummember(String newsummember) {
		this.newsummember = newsummember;
	}
	public String getNewsumclerk() {
		return newsumclerk;
	}
	public void setNewsumclerk(String newsumclerk) {
		this.newsumclerk = newsumclerk;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	

}
