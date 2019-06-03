package com.bm.auths.groupby;

import com.bm.base.BaseEN;

public class Groupby extends BaseEN{
	private Long id;
	private String authskey;//权限
	private String organization;//组织名
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAuthskey() {
		return authskey;
	}
	public void setAuthskey(String authskey) {
		this.authskey = authskey;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	@Override
	public String toString() {
		return"Groupbyid"+id+"authskey"+authskey+"organization"+organization;
	}

}
