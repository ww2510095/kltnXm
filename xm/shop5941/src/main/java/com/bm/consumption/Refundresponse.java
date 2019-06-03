package com.bm.consumption;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Refundresponse extends BaseEN{
	public enum Refundresponse_type{
		ZFB,WX,KLB
	}
	
	
	private Long id;
	private String ordercode;//订单号或id
	private String responsecode;//第三方响应代码
	private String type;//Refundresponse_type
	private BigDecimal price;//金额
	private BigDecimal ordernum;//订单金额
	private Integer istrue;//是否成功
	
	
	
	public BigDecimal getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(BigDecimal ordernum) {
		this.ordernum = ordernum;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
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
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getResponsecode() {
		return responsecode;
	}
	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Refundresponseid" + id + "1ordercode" + ordercode + "1responsecode" + responsecode + "1type" + type
				+ "1price" + price + "1ordernum" + ordernum + "1istrue" + istrue;
	}
	
}
