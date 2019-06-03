package com.bm.shoppingcard;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class ShoppingCard extends BaseEN{
	
	
	@Override
	public String toString() {return"ShoppingCardid"+id+"itemid"+itemid+"num"+num+"title"+title+"price"+price+"totalfee"+totalfee+"picpath"+picpath+"type1"+type1+"type2"+type2+"type3"+type3+"type4"+type4+"memberid"+memberid;
}
	private Long id; //id
	private Long itemid; //商品ID
	private Integer num; //数量
	private String title; //商品标题
	private BigDecimal price; //商品单价
	private BigDecimal totalfee; //商品总价
	private String picpath; //图片地址
	private String type1; //大类型
	private String type2; //中类型
	private String type3; //小类型
	private String type4; //细类型
	private String onephone; //供应商账号
	private String oneshopname; //供应商名字
	private Long memberid; //所属人
	private Long kid; //kid
	
	
	public String getOnephone() {
		return onephone;
	}
	public void setOnephone(String onephone) {
		this.onephone = onephone;
	}
	public String getOneshopname() {
		return oneshopname;
	}
	public void setOneshopname(String oneshopname) {
		this.oneshopname = oneshopname;
	}
	public Long getKid() {
		return kid;
	}
	public void setKid(Long kid) {
		this.kid = kid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getItemid() {
		return itemid;
	}
	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}

	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(BigDecimal totalfee) {
		this.totalfee = totalfee;
	}
	public String getPicpath() {
		return picpath;
	}
	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getType2() {
		return type2;
	}
	public void setType2(String type2) {
		this.type2 = type2;
	}
	public String getType3() {
		return type3;
	}
	public void setType3(String type3) {
		this.type3 = type3;
	}
	public String getType4() {
		return type4;
	}
	public void setType4(String type4) {
		this.type4 = type4;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	
	
	

}
