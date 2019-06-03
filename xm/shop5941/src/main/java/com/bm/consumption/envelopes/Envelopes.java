package com.bm.consumption.envelopes;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Envelopes extends BaseEN{
	private Long id;//id
	private String wid;//微信id
	private Integer istrue;//是否使用
	private BigDecimal num;//金额
	private Long  shopid;//核销店铺
	private String  shopname;//核销店铺
	private String phone;//核销人
	private Long  addtime;//领取时间
	private String  erpsign;//erp编号
	
	
	
	
	public String getErpsign() {
		return erpsign;
	}
	public void setErpsign(String erpsign) {
		this.erpsign = erpsign;
	}
	public Long getId() {
		return id;
	}
	public Long getAddtime() {
		return addtime;
	}
	public void setAddtime(Long addtime) {
		this.addtime = addtime;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	public Long getShopid() {
		return shopid;
	}
	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "Envelopesid" + id + "1wid" + wid + "1istrue" + istrue + "1num" + num + "1shopid" + shopid + "1shopname"
				+ shopname + "1phone" + phone + "1addtime" + addtime;
	}

	

}
