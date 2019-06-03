package com.bm.stock;

import com.bm.base.BaseEN;

public class Stock extends BaseEN{
	
	
	private Long id; //id
	private String shopcode; //店铺编号
	private Integer num; //库存量
	private String code; //条码
	private Integer type; //状态：1正常，-1，已下架，-2已冻结
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getShopcode() {
		return shopcode;
	}
	public void setShopcode(String shopcode) {
		this.shopcode = shopcode;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Override
	public String toString() {return"Stockid"+id+"shopcode"+shopcode+"num"+num+"code"+code+"type"+type;
}
	
	
}
