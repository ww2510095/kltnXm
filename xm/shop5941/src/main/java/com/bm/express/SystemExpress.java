package com.bm.express;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class SystemExpress extends BaseEN{
	
	private String id; //运单号
	private Long time; //添加时间
	private BigDecimal payment; //赔付金额
	private Long oneid; //供应商id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	public Long getOneid() {
		return oneid;
	}
	public void setOneid(Long oneid) {
		this.oneid = oneid;
	}
	@Override
	public String toString() {
		return "SystemExpressid" + id + "1time" + time + "1payment" + payment + "1oneid" + oneid;
	}

}
