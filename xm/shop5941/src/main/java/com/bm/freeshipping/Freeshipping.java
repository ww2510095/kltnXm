package com.bm.freeshipping;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Freeshipping extends BaseEN{
	private Long id; //id
	private Long oneid; //供应商id
	private Integer freeshippingsize; //多少件包邮
	private BigDecimal freeshippingnumber; //多少钱包邮
	private BigDecimal orders; //邮费多少
	private Long str; //生效时间
	private Long end; //结束时间
	
	
	
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
	public Integer getFreeshippingsize() {
		return freeshippingsize;
	}
	public void setFreeshippingsize(Integer freeshippingsize) {
		this.freeshippingsize = freeshippingsize;
	}
	public BigDecimal getFreeshippingnumber() {
		return freeshippingnumber;
	}
	public void setFreeshippingnumber(BigDecimal freeshippingnumber) {
		this.freeshippingnumber = freeshippingnumber;
	}
	public BigDecimal getOrders() {
		return orders;
	}
	public void setOrders(BigDecimal orders) {
		this.orders = orders;
	}
	@Override
	public String toString() {
		return "Freeshippingid" + id + "1oneid" + oneid + "1freeshippingsize" + freeshippingsize + "1freeshippingnumber"
				+ freeshippingnumber + "1orders" + orders;
	}

}
