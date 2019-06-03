package com.bm.commodity.back;

import com.bm.base.BaseEN;

public class Commodityback extends BaseEN{

	private Long id; //id
	private String mycode; //自己的条码
	private String youcode; //别人的条码
	private String largeclass; //大类
	private String inclass; //中类
	private String smallclass; //小类
	private String fineclass; //细类
	private String name; //商品名字
	private String supplier; //供应商
	private String brand; //品牌
	private String introduction; //商品简介
	private String detailed; //详细介绍
	private String company; //计量单位
	private String packingmethod; //包装方式
	private String originalprice; //原价
	private String price; //售价
	private String costprice; //成本价
	private String colour; //颜色
	private String mysize; //尺码
	private String mainimage; //主图数组
	private String detailsimage; //详情图数组
	private String adminphone; //导入者账号
	private String specifications; ////0：尺码，1：规格
	
	
	
	
	public String getSpecifications() {
		return specifications;
	}
	public void setSpecifications(String specifications) {
		this.specifications = specifications;
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
	public String getMycode() {
		return mycode;
	}
	public void setMycode(String mycode) {
		this.mycode = mycode;
	}
	public String getYoucode() {
		return youcode;
	}
	public void setYoucode(String youcode) {
		this.youcode = youcode;
	}
	public String getLargeclass() {
		return largeclass;
	}
	public void setLargeclass(String largeclass) {
		this.largeclass = largeclass;
	}
	public String getInclass() {
		return inclass;
	}
	public void setInclass(String inclass) {
		this.inclass = inclass;
	}
	public String getSmallclass() {
		return smallclass;
	}
	public void setSmallclass(String smallclass) {
		this.smallclass = smallclass;
	}
	public String getFineclass() {
		return fineclass;
	}
	public void setFineclass(String fineclass) {
		this.fineclass = fineclass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getDetailed() {
		return detailed;
	}
	public void setDetailed(String detailed) {
		this.detailed = detailed;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPackingmethod() {
		return packingmethod;
	}
	public void setPackingmethod(String packingmethod) {
		this.packingmethod = packingmethod;
	}
	public String getOriginalprice() {
		return originalprice;
	}
	public void setOriginalprice(String originalprice) {
		this.originalprice = originalprice;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCostprice() {
		return costprice;
	}
	public void setCostprice(String costprice) {
		this.costprice = costprice;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getMysize() {
		return mysize;
	}
	public void setMysize(String mysize) {
		this.mysize = mysize;
	}
	public String getMainimage() {
		return mainimage;
	}
	public void setMainimage(String mainimage) {
		this.mainimage = mainimage;
	}
	public String getDetailsimage() {
		return detailsimage;
	}
	public void setDetailsimage(String detailsimage) {
		this.detailsimage = detailsimage;
	}
	@Override
	public String toString() {return"Commoditybackid"+id+"mycode"+mycode+"youcode"+youcode+"largeclass"+largeclass+"inclass"+inclass+"smallclass"+smallclass+"fineclass"+fineclass+"name"+name+"supplier"+supplier+"brand"+brand+"introduction"+introduction+"detailed"+detailed+"company"+company+"packingmethod"+packingmethod+"originalprice"+originalprice+"price"+price+"costprice"+costprice+"colour"+colour+"mysize"+mysize+"mainimage"+mainimage+"detailsimage"+detailsimage+"adminphone"+adminphone;
}




}
