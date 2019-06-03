package com.bm.file;

import com.bm.base.BaseEN;

public class MyFile extends BaseEN{
	
	@Override
	public String toString() {return"MyFileid"+id+"path"+path+"md5"+md5+"filedate"+filedate+"suffix"+suffix;
}
	private Long id;
	private String path;//文件路径
	private String md5;//md5
	private String filedate;//日期
	private String suffix;//后缀名
	
	
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public  String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
