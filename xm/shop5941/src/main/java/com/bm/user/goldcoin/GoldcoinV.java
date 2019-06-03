package com.bm.user.goldcoin;

import com.bm.base.BaseEN;

/*
 * 积分明细
 * */
public class GoldcoinV extends BaseEN{
	private Long id;
	private Integer num;//分
	private String title;//说明
	private Long memberid;//用户
	private Integer istrue;//是否生效
	private String b_y;//备用字段，暂时只存储订单id
	
	
	
	
	public String getB_y() {
		return b_y;
	}
	public void setB_y(String b_y) {
		this.b_y = b_y;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "GoldcoinVid" + id + "1num" + num + "1title" + title + "1memberid" + memberid + "1istrue" + istrue
				+ "1by" + b_y;
	}
	
	
	
	
}
