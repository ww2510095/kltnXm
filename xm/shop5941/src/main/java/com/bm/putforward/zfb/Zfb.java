package com.bm.putforward.zfb;

import com.bm.base.BaseEN;

/**
 * 绑定支付宝
 */
public class Zfb extends BaseEN{
	private Long id;
	private String name; // 姓名
	private String zfb; // 支付宝号
	private Integer istrue; // 是否审核通过,1:是
	private String zfbcode; // 验证码
	private String phone; // 申请用户
	private Long memberid; // 申请用户id
	
	
	
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getZfb() {
		return zfb;
	}
	public void setZfb(String zfb) {
		this.zfb = zfb;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public String getZfbcode() {
		return zfbcode;
	}
	public void setZfbcode(String zfbcode) {
		this.zfbcode = zfbcode;
	}
	@Override
	public String toString() {
		return "Zfbid" + id + "1name" + name + "1zfb" + zfb + "1istrue" + istrue + "1zfbcode" + zfbcode + "1phone"
				+ phone + "1memberid" + memberid;
	}
	
}
