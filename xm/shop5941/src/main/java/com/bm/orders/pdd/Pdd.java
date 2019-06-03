package com.bm.orders.pdd;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Pdd extends BaseEN{
	
	private String bh;//拼团编号
	private Integer type;//拼团类型
	
	public enum ptfaenum{
		DDL(1),//订单数量
		RS(2),//人数
		SPSL(3);//商品数量
		private ptfaenum(int i) {
			type=i;
		}
		private int type;
		public int getValue(){
			return type;
		}
	}
	

	public String getBh() {
		return bh;
	}

	public void setBh(String bh) {
		this.bh = bh;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	private Long id;
	private String title;//标题
	private Integer num;//剩余数量
	private Long star;//开始时间
	private Long end;//结束时间
	private Long commoditykeyid ;//商品编号
	private String code ;//商品条码
	private String imagepath ;//图片
	private BigDecimal price;//价格
	
	
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
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
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
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
	public Long getCommoditykeyid() {
		return commoditykeyid;
	}
	public void setCommoditykeyid(Long commoditykeyid) {
		this.commoditykeyid = commoditykeyid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Msid" + id + "1title" + title + "1num" + num + "1star" + star + "1end" + end + "1Commoditykeyid"
				+ commoditykeyid + "1code" + code + "1price" + price;
	}
	
	
	


	

}
