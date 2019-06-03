package com.bm.evaluate;

import com.bm.base.BaseEN;

public class Evaluate extends BaseEN{
	
	
	@Override
	public String toString() {return"Evaluateid"+id+"ordersid"+ordersid+"num"+num+"content"+content;
}
	private Long id; //id
	private Long ordersid; //评价订单
	private Integer num; //星级
	private String content; //评价语
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrdersid() {
		return ordersid;
	}
	public void setOrdersid(Long ordersid) {
		this.ordersid = ordersid;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	


}
