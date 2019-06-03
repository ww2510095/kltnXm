package com.bm.base.redis;

/**
 * 用户当前登录状态
 * 
 * */
public class RedisMemberType {
	private Long userid;//用户id
	private String Uname;//用户账号
	private Integer type;//登录设备，1：web，2：手机，3：客户端，4：其他设备
	private String UID;//当前设备id
	private String WEBID;//当前设备id
	private String time;//最后访问时间
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWEBID() {
		return WEBID;
	}
	public void setWEBID(String wEBID) {
		WEBID = wEBID;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getUname() {
		return Uname;
	}
	public void setUname(String uname) {
		Uname = uname;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	
}
