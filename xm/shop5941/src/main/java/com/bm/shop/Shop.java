package com.bm.shop;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Shop extends BaseEN{
	
	@Override
	public String toString() {return"Shopid"+id+"shopname"+shopname+"shoptype"+shoptype+"memberid"+memberid+"superid"+superid+"systemtype"+systemtype+"shopstate"+shopstate+"shoplogo"+shoplogo+"addtime"+addtime+"systemadmin"+systemadmin+"systemopinion"+systemopinion+"shopresume"+shopresume+"systemtime"+systemtime+"balance"+balance+"province"+province+"city"+city+"area"+area+"street"+street+"detailed"+detailed+"longitude"+longitude+"latitude"+latitude+"oneid"+oneid+"code"+code;
}
	private Long id;//is 'id';
	private String shopname;// is '名字';
	private String shoptype;//is '类型';
	private Long memberid;//is '用户';
	private Long superid;//is '父级id';0代表系统
	private String systemtype;// is '所属系统';
	private Integer shopstate;//;is '状态';0;提交，1：正常，2,关闭，-1：未通过，-2：冻结
	private String shoplogo;//;is 'logo图';
	private Long addtime;//is '申请时间';
	private String systemadmin;//is '审核管理员';
	private String systemopinion;// is '审核意见';
	private String shopresume;//is '店铺简介';
	private Long systemtime;// is '审核时间';
	private BigDecimal balance;// is '店铺余额';
	
	private String province;// is '省';//||经营片区
	private String city;// 市
	private String area;//区
	private String street;// 街道
	private String detailed;//详细地址
	private BigDecimal longitude;// 经度
	private BigDecimal latitude;// 纬度
	private Long oneid;//is 代理商id
	
	private String code;//店铺编号
	private String shopphone;//客服电话
	private Integer csl;//袜子数量
	
	
	///////////////关联的表
	
	private String shopuname;//店铺账号

	
	
	
	
	
	
	
	public Integer getCsl() {
		return csl;
	}
	public void setCsl(Integer csl) {
		this.csl = csl;
	}
	public String getShopuname() {
		return shopuname;
	}
	public void setShopuname(String shopuname) {
		this.shopuname = shopuname;
	}
	public String getShopphone() {
		return shopphone;
	}
	public void setShopphone(String shopphone) {
		this.shopphone = shopphone;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getOneid() {
		return oneid;
	}
	public void setOneid(Long oneid) {
		this.oneid = oneid;
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
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
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
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getSuperid() {
		return superid;
	}
	public void setSuperid(Long superid) {
		this.superid = superid;
	}
	public String getSystemtype() {
		return systemtype;
	}
	public void setSystemtype(String systemtype) {
		this.systemtype = systemtype;
	}
	public Integer getShopstate() {
		return shopstate;
	}
	public void setShopstate(Integer shopstate) {
		this.shopstate = shopstate;
	}
	public String getShoplogo() {
		return shoplogo;
	}
	public void setShoplogo(String shoplogo) {
		this.shoplogo = shoplogo;
	}
	public Long getAddtime() {
		return addtime;
	}
	public void setAddtime(Long addtime) {
		this.addtime = addtime;
	}
	public String getSystemadmin() {
		return systemadmin;
	}
	public void setSystemadmin(String systemadmin) {
		this.systemadmin = systemadmin;
	}
	public String getSystemopinion() {
		return systemopinion;
	}
	public void setSystemopinion(String systemopinion) {
		this.systemopinion = systemopinion;
	}
	public String getShopresume() {
		return shopresume;
	}
	public void setShopresume(String shopresume) {
		this.shopresume = shopresume;
	}
	public Long getSystemtime() {
		return systemtime;
	}
	public void setSystemtime(Long systemtime) {
		this.systemtime = systemtime;
	}
	
}
