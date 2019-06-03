package com.bm.promotion;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Promotion extends BaseEN{
	private Long id; //id
	private String title; //标签
	private Integer type; //促销类型1：打折，2：降价，3：满减
	private BigDecimal discount; //打折多少
	private BigDecimal reduce; //降价多少
	private BigDecimal subtraction1; //满多少减
	private BigDecimal subtraction2; //减多少
	private Long star; //开始时间
	private Long end; //结束时间
	private Long memberid; //发布人（店长）
	private String introduce; //活动介绍
	private String commodityname; //商品条码
	private String shopcode; //店铺编码
	
	
	
	
	
	public String getShopcode() {
		return shopcode;
	}

	public void setShopcode(String shopcode) {
		this.shopcode = shopcode;
	}

	@Override
	public String toString() {return"Promotionid"+id+"title"+title+"type"+type+"discount"+discount+"reduce"+reduce+"subtraction1"+subtraction1+"subtraction2"+subtraction2+"star"+star+"end"+end+"memberid"+memberid+"introduce"+introduce+"Commodityname"+commodityname;
}
	
	public String getCommodityname() {
		return commodityname;
	}

	public void setCommodityname(String commodityname) {
		this.commodityname = commodityname;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public BigDecimal getReduce() {
		return reduce;
	}
	public void setReduce(BigDecimal reduce) {
		this.reduce = reduce;
	}
	public BigDecimal getSubtraction1() {
		return subtraction1;
	}
	public void setSubtraction1(BigDecimal subtraction1) {
		this.subtraction1 = subtraction1;
	}
	public BigDecimal getSubtraction2() {
		return subtraction2;
	}
	public void setSubtraction2(BigDecimal subtraction2) {
		this.subtraction2 = subtraction2;
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
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	
	

}
