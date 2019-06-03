package com.bm.evaluate;

public class EvaluateSelect extends Evaluate{
	
	private String portrait;//评价人头像
	private Long memberid;//评价人
	private String membernickname;//评价昵称
	private String memberphone;//评价账号
	private Long adminshopid;//订单所属店铺id
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getMembernickname() {
		return membernickname;
	}
	public void setMembernickname(String membernickname) {
		this.membernickname = membernickname;
	}
	public String getMemberphone() {
		return memberphone;
	}
	public void setMemberphone(String memberphone) {
		this.memberphone = memberphone;
	}
	public Long getAdminshopid() {
		return adminshopid;
	}
	public void setAdminshopid(Long adminshopid) {
		this.adminshopid = adminshopid;
	}
	
	
	
	

}
