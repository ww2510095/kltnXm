package com.bm.wx.xuankuan.stock;

public class Bz {
	
	private String date;//时间
	private String name;//人
	private String a;//内容
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	@Override
	public String toString() {
		return "Bzdate" + date + "1name" + name + "1a" + a;
	}
	
	

}
