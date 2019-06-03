package com.bm.zsh;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

/**
 * 招商会专用订单
 * */
public class Zsh extends BaseEN{
	private String id;
	private Long memberid;//用户id
	private String phone;//电话
	private BigDecimal money;//金额
	private Integer type;//1:经销商，2，线上店主
	private Integer statis;//1:已付款，0，未付款
	private Long updatetime;//更新付费时间
	private Integer istrue;//是否激活
	
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getStatis() {
		return statis;
	}
	public void setStatis(Integer statis) {
		this.statis = statis;
	}
	public Long getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Long updatetime) {
		this.updatetime = updatetime;
	}
	

}
