package com.bm.consumption.envelopes;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class EnvelopesKey extends BaseEN{
	private Long id;//id
	private BigDecimal maxnum;//红包最高金额
	private BigDecimal minnum;//红包最低金额
	private BigDecimal num_price;//两个红包的金额和
	private Integer  maxsize;//每个人每天领取数量
	private Integer  maxt;//最高金额红包为第几个
	
	public Integer getMaxt() {
		return maxt;
	}
	public void setMaxt(Integer maxt) {
		this.maxt = maxt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getMaxnum() {
		return maxnum;
	}
	public void setMaxnum(BigDecimal maxnum) {
		this.maxnum = maxnum;
	}
	public BigDecimal getMinnum() {
		return minnum;
	}
	public void setMinnum(BigDecimal minnum) {
		this.minnum = minnum;
	}
	public BigDecimal getNum_price() {
		return num_price;
	}
	public void setNum_price(BigDecimal num_price) {
		this.num_price = num_price;
	}
	public Integer getMaxsize() {
		return maxsize;
	}
	public void setMaxsize(Integer maxsize) {
		this.maxsize = maxsize;
	}
	@Override
	public String toString() {
		return "EnvelopesKeyid" + id + "1maxnum" + maxnum + "1minnum" + minnum + "1num_price" + num_price + "1maxsize"
				+ maxsize + "1maxt" + maxt;
	}
	
	
	
	

}
