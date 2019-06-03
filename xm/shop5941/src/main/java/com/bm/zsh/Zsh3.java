package com.bm.zsh;

import com.bm.base.BaseEN;

public class Zsh3 extends BaseEN{
	private Long id ;
	private String phone ;
	private Integer num ;
	private String name;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Zsh3id" + id + "1phone" + phone + "1num" + num + "1name" + name;
	}
	
	

}
