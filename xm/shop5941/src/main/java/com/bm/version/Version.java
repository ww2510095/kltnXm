package com.bm.version;

import com.bm.base.BaseEN;

public class Version extends BaseEN{
	
	 private Long id;
	 private String code;//版本号
	 private String  data;//更新内容
	 private String   url;//下载地址
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	 
}
