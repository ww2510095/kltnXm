package com.bm.help;

import com.bm.base.BaseEN;

public class Help extends BaseEN{
	private Long id;
	private String title1;//上一级标题
	private String title2;//标题
	private String content;//1：标题，其他是内容
	private Integer type;//帮助等级，普通用户为0级
	private Integer px;//排序
	
	
	
	public Integer getPx() {
		return px;
	}
	public void setPx(Integer px) {
		this.px = px;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle1() {
		return title1;
	}
	public void setTitle1(String title1) {
		this.title1 = title1;
	}
	public String getTitle2() {
		return title2;
	}
	public void setTitle2(String title2) {
		this.title2 = title2;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Helpid" + id + "1title1" + title1 + "1title2" + title2 + "1content" + content;
	}
	

}
