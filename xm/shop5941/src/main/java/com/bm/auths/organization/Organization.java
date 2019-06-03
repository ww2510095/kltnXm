package com.bm.auths.organization;

import com.bm.base.BaseEN;

public class Organization extends BaseEN{
	private Long id;
	private String groupbyname;//组织名字
	private Long memberid;//用户id
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
	
	public String getGroupbyname() {
		return groupbyname;
	}
	public void setGroupbyname(String groupbyname) {
		this.groupbyname = groupbyname;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	@Override
	public String toString() {
		return"Organizationid"+id+"groupbyname"+groupbyname+"memberid"+memberid;
	}
	
}
