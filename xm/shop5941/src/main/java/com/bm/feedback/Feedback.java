package com.bm.feedback;

import com.bm.base.BaseEN;

public class Feedback extends BaseEN{
	private Long id;
	private String text;//反馈意见
	private String phone;//反馈人账号
	
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
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
		return "Feedbackid" + id + "1text" + text + "1phone" + phone;
	}
	
	

}
