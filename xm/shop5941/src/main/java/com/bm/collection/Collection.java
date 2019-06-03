package com.bm.collection;

import com.bm.base.BaseEN;

public class Collection extends BaseEN{
	private Long id; //id
	private Long commodityid; //商品id
	private Long memberid; //用户id
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCommodityid() {
		return commodityid;
	}
	public void setCommodityid(Long commodityid) {
		this.commodityid = commodityid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	@Override
	public String toString() {return"Collectionid"+id+"commodityid"+commodityid+"memberid"+memberid;
}
	
	


}
