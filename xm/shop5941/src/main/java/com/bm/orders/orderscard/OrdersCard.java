package com.bm.orders.orderscard;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class OrdersCard extends BaseEN{
	
	private Long id;
	private String title;//说明
	private BigDecimal num;//剩余额度
	private BigDecimal nummax;//初始额度
	private Long memberid;//绑定用户
	private Long star;//开始时间
	private Long end;//结束时间
	private String code;//卡号
	private String pwd;//密码
	private String bcode;//批量编号
	
	
	
	
	public String getBcode() {
		return bcode;
	}
	public void setBcode(String bcode) {
		this.bcode = bcode;
	}
	public BigDecimal getNummax() {
		return nummax;
	}
	public void setNummax(BigDecimal nummax) {
		this.nummax = nummax;
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
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	@Override
	public String toString() {
		return "OrdersCardid" + id + "1title" + title + "1num" + num + "1nummax" + nummax + "1memberid" + memberid
				+ "1star" + star + "1end" + end + "1code" + code + "1pwd" + pwd + "1bcode" + bcode;
	}



}
