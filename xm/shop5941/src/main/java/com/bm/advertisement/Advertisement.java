package com.bm.advertisement;

import com.bm.base.BaseEN;

public class Advertisement extends BaseEN{
	private Long id; //id
	private String describe; //图片描述/主名字/购物车/分类名字
	private String url; //点击跳转页面标识
	private Long pagekey; //参数
	private String path; //图片地址
	private String key; //请求标识0：首页广告，1：首页轮播图,2:首页弹出广告图,-1,大分类轮播图，,4,启动页轮播图
	//-2：细分类轮播图,-3细类小图,3商品列表广告图,4,首页轮播图下面的广告图,11 我要合作页面经销商商广告图，12我要合作页面线上店主广告图
	private String bys;//细类对应大类名字
	private String orderby;//排序，-1为隐藏
	
	
	
	
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getBys() {
		return bys;
	}
	public void setBys(String bys) {
		this.bys = bys;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getPagekey() {
		return pagekey;
	}
	public void setPagekey(Long pagekey) {
		this.pagekey = pagekey;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Advertisementid="+id+"describe="+describe+"url="+url+"pagekey="+pagekey+"path="+path+"key="+key;
	}
	
	


}
