package com.bm.express;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Express extends BaseEN{
	private Long id; //id
	private Long oneid; //供应商id
	private BigDecimal maxindemnity; //最高赔付
	private BigDecimal maxindemnitya; //单笔最高赔付
	private BigDecimal minindemnitya; //单笔最低赔付
	private BigDecimal payment; //单笔价格
	private Long str; //生效时间
	private Long end; //结束时间
	
	
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOneid() {
		return oneid;
	}
	public void setOneid(Long oneid) {
		this.oneid = oneid;
	}
	public BigDecimal getMaxindemnity() {
		return maxindemnity;
	}
	public void setMaxindemnity(BigDecimal maxindemnity) {
		this.maxindemnity = maxindemnity;
	}
	public BigDecimal getMaxindemnitya() {
		return maxindemnitya;
	}
	public void setMaxindemnitya(BigDecimal maxindemnitya) {
		this.maxindemnitya = maxindemnitya;
	}
	public BigDecimal getMinindemnitya() {
		return minindemnitya;
	}
	public void setMinindemnitya(BigDecimal minindemnitya) {
		this.minindemnitya = minindemnitya;
	}
	public Long getStr() {
		return str;
	}
	public void setStr(Long str) {
		this.str = str;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "Expressid" + id + "1oneid" + oneid + "1maxindemnity" + maxindemnity + "1maxindemnitya" + maxindemnitya
				+ "1minindemnitya" + minindemnitya + "1payment" + payment + "1str" + str + "1end" + end;
	}

	
	
}
