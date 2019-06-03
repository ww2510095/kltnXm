package com.bm.myaddress;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Myaddress extends BaseEN{
	
	
	@Override
	public String toString() {return"Myaddressid"+id+"province"+province+"city"+city+"area"+area+"street"+street+"detailed"+detailed+"longitude"+longitude+"latitude"+latitude+"istrue"+istrue+"memberid"+memberid+"name"+name+"phone"+phone+"code"+code;
}
	private Long id;//id
	private String province; //省
	private String city; //市
	private String area; //区
	private String street; //街道
	private String detailed; //详细地址
	private BigDecimal longitude; //经度
	private BigDecimal latitude; //纬度
	private Integer istrue; //是否是默认收货地址，1：是
	private Long memberid;//用户id
	
	private String name; //收货人名字
	private String phone; //收货人电话
	private String code; //邮编
	
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getDetailed() {
		return detailed;
	}
	public void setDetailed(String detailed) {
		this.detailed = detailed;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}


	
	
}
