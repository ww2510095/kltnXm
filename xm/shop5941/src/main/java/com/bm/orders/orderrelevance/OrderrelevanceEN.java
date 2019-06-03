package com.bm.orders.orderrelevance;

import com.bm.promotion.Promotion;

public class OrderrelevanceEN extends Orderrelevance{
	private String colour;
	private String mysize;
	private Promotion mPromotion;
	
	private String oneA; //供应商账号
	private String oneB; //供应商名字
	private Integer asize; //限购数量+
	
	
	public Integer getAsize() {
		return asize;
	}
	public void setAsize(Integer asize) {
		this.asize = asize;
	}
	public String getOneA() {
		return oneA;
	}
	public void setOneA(String oneA) {
		this.oneA = oneA;
	}
	public String getOneB() {
		return oneB;
	}
	public void setOneB(String oneB) {
		this.oneB = oneB;
	}
	public Promotion getmPromotion() {
		return mPromotion;
	}
	public void setmPromotion(Promotion mPromotion) {
		this.mPromotion = mPromotion;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getMysize() {
		return mysize;
	}
	public void setMysize(String mysize) {
		this.mysize = mysize;
	}
	

}
