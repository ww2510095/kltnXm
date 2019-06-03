package com.bm.orders.cj.jc;

import com.bm.base.BaseEN;

public class Cj_address extends BaseEN{
	private Long id;
	private Long member_id;//用户
	private String member_name;//名字
	private String member_address;//地址
	private String member_phone;//电话
	private Long jc_id;//奖品
	private String zip_code;//快递号
	
	
	
	public String getZip_code() {
		return zip_code;
	}



	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getMember_id() {
		return member_id;
	}



	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}



	public String getMember_name() {
		return member_name;
	}



	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}



	public String getMember_address() {
		return member_address;
	}



	public void setMember_address(String member_address) {
		this.member_address = member_address;
	}



	public String getMember_phone() {
		return member_phone;
	}



	public void setMember_phone(String member_phone) {
		this.member_phone = member_phone;
	}



	public Long getJc_id() {
		return jc_id;
	}



	public void setJc_id(Long jc_id) {
		this.jc_id = jc_id;
	}



	@Override
	public String toString() {
		return "Cj_addressid" + id + "1member_id" + member_id + "1member_name" + member_name + "1member_address"
				+ member_address + "1member_phone" + member_phone + "1jc_id" + jc_id + "1zip_code" + zip_code;
	}



	
	
	
	

}
