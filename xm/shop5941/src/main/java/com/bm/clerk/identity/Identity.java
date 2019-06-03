package com.bm.clerk.identity;

import com.bm.base.BaseEN;

public class Identity extends BaseEN{
	
	private Long id;//id
	private Long memberid;//用户id
	private Integer type;//身份，1：经销商，2：线上店主
	private Long suid;//上级id
	private Long shopid;//所属店铺id
	private Integer onetype;//上一次身份，1：普通用户，2：导购，3：经销商，4：线上店主
	private Long oneshopid;//上一次所属店铺id
	private Integer system;//审核状态，-1：已拒绝，0，审核中，1：已通过,2:已停用
	private Long star;//开始时间
	private Long end;//结束时间
	
	private String shopname;
	
	
	
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public Integer getSystem() {
		return system;
	}
	public void setSystem(Integer system) {
		this.system = system;
	}
	public Long getStar() {
		return star;
	}
	public void setStar(Long star) {
		this.star = star;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	public Long getShopid() {
		return shopid;
	}
	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}
	public Integer getOnetype() {
		return onetype;
	}
	public void setOnetype(Integer onetype) {
		this.onetype = onetype;
	}
	public Long getOneshopid() {
		return oneshopid;
	}
	public void setOneshopid(Long oneshopid) {
		this.oneshopid = oneshopid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getSuid() {
		return suid;
	}
	public void setSuid(Long suid) {
		this.suid = suid;
	}
	

}
