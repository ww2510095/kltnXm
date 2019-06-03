package com.bm.shop.back;

import com.bm.base.BaseEN;

public class Shopback extends BaseEN{
	
	private Long id;//is 'id';
	private String shopname;//is '名字';
	private String shoptype;//is '类型';
	private String memberid;//is '用户';
	private String superid;//is '父级id';
	private String systemtype;//is '所属系统';
	private String addtime;//is '导入时间';
	private String shopresume;//is '店铺简介';
	private String province;//is '省';
	private String city;//is '市';
	private String area;//is '区';
	private String street;//is '街道';
	private String detailed;//is '详细地址';
	private String longitude;//is '经度';
	private String latitude;//is '纬度';
	private String oneid;//is '代理商id';
	private String logo;//is logo
	private String adminphone;//导入者
	private String code;//店铺编号
	
	
	
	
	
	@Override
	public String toString() {return"Shopbackid"+id+"shopname"+shopname+"shoptype"+shoptype+"memberid"+memberid+"superid"+superid+"systemtype"+systemtype+"addtime"+addtime+"shopresume"+shopresume+"province"+province+"city"+city+"area"+area+"street"+street+"detailed"+detailed+"longitude"+longitude+"latitude"+latitude+"oneid"+oneid+"logo"+logo+"adminphone"+adminphone+"code"+code;
}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getAdminphone() {
		return adminphone;
	}
	public void setAdminphone(String adminphone) {
		this.adminphone = adminphone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public String getShoptype() {
		return shoptype;
	}
	public void setShoptype(String shoptype) {
		this.shoptype = shoptype;
	}
	public String getMemberid() {
		return memberid;
	}
	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}
	public String getSuperid() {
		return superid;
	}
	public void setSuperid(String superid) {
		this.superid = superid;
	}
	public String getSystemtype() {
		return systemtype;
	}
	public void setSystemtype(String systemtype) {
		this.systemtype = systemtype;
	}

	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getShopresume() {
		return shopresume;
	}
	public void setShopresume(String shopresume) {
		this.shopresume = shopresume;
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
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getOneid() {
		return oneid;
	}
	public void setOneid(String oneid) {
		this.oneid = oneid;
	}
	
	

}
