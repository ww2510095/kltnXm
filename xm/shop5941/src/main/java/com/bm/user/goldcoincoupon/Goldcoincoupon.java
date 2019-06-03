package com.bm.user.goldcoincoupon;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Goldcoincoupon extends BaseEN{
	
	private Long id; //id
	private String title; //说明
	private BigDecimal numbera; //面值
	private BigDecimal numbermin; //满多少使用
	private Long star; //开始时间
	private Long end; //结束时间
	private Integer type; //使用类型，1：通用卷，2：互斥卷
	private Integer state; //状态，0：未使用，1：已使用
	private Integer cardtype; //卷类型：1：单品，2：细类，3：小类，4：中类，5：大类,6全部
	private Integer key; //单品类型，1：一个商品，2：一款商品
	private String data; //条码或类名
	private Long memberid; //所属人
	private String mphone; //发放人
	private String onephone; //所属供应商
	private Integer Goldcoinnum; //所需积分
	private Integer istrue; //是否可用
	
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
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
	public BigDecimal getNumbera() {
		return numbera;
	}
	public void setNumbera(BigDecimal numbera) {
		this.numbera = numbera;
	}
	public BigDecimal getNumbermin() {
		return numbermin;
	}
	public void setNumbermin(BigDecimal numbermin) {
		this.numbermin = numbermin;
	}
	public Long getStar() {
		return star;
	}
	public void setStar(Long star) {
		this.star = star;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getCardtype() {
		return cardtype;
	}
	public void setCardtype(Integer cardtype) {
		this.cardtype = cardtype;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getMphone() {
		return mphone;
	}
	public void setMphone(String mphone) {
		this.mphone = mphone;
	}
	public String getOnephone() {
		return onephone;
	}
	public void setOnephone(String onephone) {
		this.onephone = onephone;
	}
	public Integer getGoldcoinnum() {
		return Goldcoinnum;
	}
	public void setGoldcoinnum(Integer goldcoinnum) {
		Goldcoinnum = goldcoinnum;
	}
	@Override
	public String toString() {
		return "Goldcoincouponid" + id + "1title" + title + "1numbera" + numbera + "1numbermin" + numbermin + "1star"
				+ star + "1end" + end + "1type" + type + "1state" + state + "1cardtype" + cardtype + "1key" + key
				+ "1data" + data + "1memberid" + memberid + "1mphone" + mphone + "1onephone" + onephone + "1Goldcoinnum"
				+ Goldcoinnum + "1istrue" + istrue;
	}
	
	
	
	
	
}
