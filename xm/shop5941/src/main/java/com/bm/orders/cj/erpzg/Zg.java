package com.bm.orders.cj.erpzg;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Zg extends BaseEN{
	private Long id;
	private BigDecimal num;//订单金额
	private Integer istrue;//是否抽奖
	private String erp_number_code;//erp订单号
	private String phone;//账号
	
	
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
	public String getErp_number_code() {
		return erp_number_code;
	}
	public void setErp_number_code(String erp_number_code) {
		this.erp_number_code = erp_number_code;
	}
	
	

}
