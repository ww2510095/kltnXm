package com.bm.consumption;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Consumption extends BaseEN{
	private Long id;//is 'id';
	private Long memberid;// is '用户id';
	private BigDecimal originalprice;// is '消费金额';,小于0代表充值
	private Long time;//  is '消费生成时间';支付时间
	private String introduce;//is '消费介绍（充值，消费，提现，转账，平台对接等等）';
	private Integer type;//账单类型：1，个人账单，2：店铺账单
	private Integer state;//账单状态：0：正常，1，已删除
	
	
	
	@Override
	public String toString() {return"Consumptionid"+id+"memberid"+memberid+"originalprice"+originalprice+"time"+time+"introduce"+introduce+"type"+type+"state"+state;
}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public BigDecimal getOriginalprice() {
		return originalprice;
	}
	public void setOriginalprice(BigDecimal originalprice) {
		this.originalprice = originalprice;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	
}
