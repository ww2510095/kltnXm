package com.bm.commodity;

/**
 * 商品类,包括库存
 * @author Administrator
 *
 */
public class CommodityNum extends Commodity{
	
	private Integer num;//库存量
	private String shopname;//店铺名字  空的为合并店铺
	private Long shopid;//店铺id 空的位合并店铺
	
	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public Long getShopid() {
		return shopid;
	}

	public void setShopid(Long shopid) {
		this.shopid = shopid;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
	
	