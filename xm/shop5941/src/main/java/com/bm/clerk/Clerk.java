package com.bm.clerk;

import com.bm.base.BaseEN;

public class Clerk extends BaseEN{
	private Long id; //id
	private Long shopid; //店铺id
	private Long memberid; //用户id
	private Integer state; //状态，1：在职，2离职
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getShopid() {
		return shopid;
	}
	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	@Override
	public String toString() {return"Clerkid"+id+"shopid"+shopid+"memberid"+memberid+"state"+state;
}
	
	
	
}
