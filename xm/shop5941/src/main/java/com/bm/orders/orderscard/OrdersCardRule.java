package com.bm.orders.orderscard;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class OrdersCardRule extends BaseEN{
	
	private Long id;
	private String code;//编号
	private BigDecimal min;//初始阈值
	private BigDecimal max;//最大阈值
	private BigDecimal num;//金额
	
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public BigDecimal getMin() {
		return min;
	}
	public void setMin(BigDecimal min) {
		this.min = min;
	}
	public BigDecimal getMax() {
		return max;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}
	@Override
	public String toString() {
		return "OrdersCardRuleid" + id + "1code" + code + "1min" + min + "1max" + max + "1num" + num;
	}

	
	

}
