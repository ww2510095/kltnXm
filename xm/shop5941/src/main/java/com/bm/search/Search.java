package com.bm.search;

import com.bm.base.BaseEN;

public class Search  extends BaseEN{
	private Long id;
	private String text;//搜索文本
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "Searchid" + id + "text" + text;
	}
	
	

}
