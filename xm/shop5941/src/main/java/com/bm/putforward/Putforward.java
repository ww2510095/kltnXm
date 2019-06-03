package com.bm.putforward;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Putforward  extends BaseEN{
	private Long id;
	private String phone;//提现用户账号
	private Long memberid;//提现用户id
	private String name;//姓名
	private String zfb;//支付宝号
	private BigDecimal num;//金额
	private Integer istrue ;//系统是否已通过
	private String imagepath ;//转账快照 
	private BigDecimal procedures ;//手续费，最低2快
	private BigDecimal money ;//实际金额
	
	
	//非字段
	private String time ;//提现时间
	private String type ;//状态
	
	
	
	
	public BigDecimal getProcedures() {
		return procedures;
	}

	public void setProcedures(BigDecimal procedures) {
		this.procedures = procedures;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	public Integer getIstrue() {
		return istrue;
	}
	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	@Override
	public String toString() {
		return "Putforwardid" + id + "1phone" + phone + "1memberid" + memberid + "1name" + name + "1zfb" + zfb + "1num"
				+ num + "1istrue" + istrue + "1imagepath" + imagepath;
	}

	

}
