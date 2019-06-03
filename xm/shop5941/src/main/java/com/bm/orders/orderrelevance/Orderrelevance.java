package com.bm.orders.orderrelevance;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

/**
 * 商品
 * */
public class Orderrelevance extends BaseEN{
	
	private Long id; //id
	private Long itemid; //商品ID
	private String ordernum; //订单号
	private Long orderid; //订单id
	private Integer num; //数量
	private String title; //商品标题
	private BigDecimal price; //商品单价
	private BigDecimal totalfee; //商品总价
	private String picpath; //图片地址
	private String type1; //大类型
	private String type2; //中类型
	private String type3; //小类型
	private String type4; //细类型
	
	private String promotionid; //促销规则
	private String promotiontitle; //促销名字
	private String youcode; //商品条码
	private String reduction; //降价多少
	
	
	
	private Long memberid; //购买人id
	private String shippingtype; //物流方式
	private String nickname; //购买人昵称
	private String phone; //购买人账号
	private String bh;//拼团编号
	private Integer gdf;//固定分成方案

	
	
	//------------非字段-------------
	private String shopid; //店铺id
	
	
	
	public Integer getGdf() {
		return gdf;
	}

	public void setGdf(Integer gdf) {
		this.gdf = gdf;
	}

	public String getBh() {
		return bh;
	}

	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getShopid() {
		return shopid;
	}
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public String getShippingtype() {
		return shippingtype;
	}
	public void setShippingtype(String shippingtype) {
		this.shippingtype = shippingtype;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getReduction() {
		return reduction;
	}
	public void setReduction(String reduction) {
		this.reduction = reduction;
	}
	public String getYoucode() {
		return youcode;
	}
	public void setYoucode(String youcode) {
		this.youcode = youcode;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public String getPromotiontitle() {
		return promotiontitle;
	}
	public void setPromotiontitle(String promotiontitle) {
		this.promotiontitle = promotiontitle;
	}
	public String getPromotionid() {
		return promotionid;
	}
	public void setPromotionid(String promotionid) {
		this.promotionid = promotionid;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getType2() {
		return type2;
	}
	public void setType2(String type2) {
		this.type2 = type2;
	}
	public String getType3() {
		return type3;
	}
	public void setType3(String type3) {
		this.type3 = type3;
	}
	public String getType4() {
		return type4;
	}
	public void setType4(String type4) {
		this.type4 = type4;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getItemid() {
		return itemid;
	}
	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}

	public String getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(BigDecimal totalfee) {
		this.totalfee = totalfee;
	}
	public String getPicpath() {
		return picpath;
	}
	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	@Override
	public String toString() {
		return "Orderrelevanceid" + id + "1itemid" + itemid + "1ordernum" + ordernum + "1orderid" + orderid + "1num"
				+ num + "1title" + title + "1price" + price + "1totalfee" + totalfee + "1picpath" + picpath + "1type1"
				+ type1 + "1type2" + type2 + "1type3" + type3 + "1type4" + type4 + "1promotionid" + promotionid
				+ "1promotiontitle" + promotiontitle + "1youcode" + youcode + "1reduction" + reduction + "1memberid"
				+ memberid + "1shippingtype" + shippingtype + "1nickname" + nickname + "1phone" + phone + "1bh" + bh
				+ "1gdf" + gdf + "1shopid" + shopid;
	}
	
}
