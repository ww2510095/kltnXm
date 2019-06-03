package com.bm.systemMessage;

import com.bm.base.BaseEN;

public class SystemMessage extends BaseEN{
	
	private Long id; //id
	private String title; //标题
	private String content; //内容
	private String summary; //摘要
	private String image; //图片
	private String code; //消息编号
	private Integer type; //类型
	private Integer istrue; //是否阅读，0，未阅读，1：已阅读
	private Long memberid; //发布人
	private Long mymemberid; //所属人，0代表所有人
	
	
	
	@Override
	public String toString() {return"SystemMessageid"+id+"title"+title+"content"+content+"summary"+summary+"image"+image+"code"+code+"type"+type+"istrue"+istrue+"memberid"+memberid+"mymemberid"+mymemberid;
}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getMymemberid() {
		return mymemberid;
	}
	public void setMymemberid(Long mymemberid) {
		this.mymemberid = mymemberid;
	}
	

}
