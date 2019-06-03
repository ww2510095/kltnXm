package com.bm.auths.auths;

import com.bm.base.BaseEN;

public class Auths extends BaseEN{
	
	private Long id;
	private String key;//key
	private String notes;//注释
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		return "Authsid"+id+"key"+key+"notes"+notes;

	}
	

}
