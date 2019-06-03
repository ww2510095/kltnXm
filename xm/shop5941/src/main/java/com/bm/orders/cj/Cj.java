package com.bm.orders.cj;

import com.bm.base.BaseEN;

public class Cj extends BaseEN{
	private Long id;
	private Integer type;//抽奖类型,1积分抽奖，2:活动抽奖，3：虚拟数据
	private Integer num;//活动抽奖剩余次数
	private Long jc_id;//抽到奖品
	private String jc_bh;//奖池编号
	private Long member_id;//抽奖人
	private String jc_name;//奖品名字
	private String t;//奖品是否已处理
	
	private String code;//优惠码
	private String su_name;//处理人
	
	
	
	
	public String getSu_name() {
		return su_name;
	}
	public void setSu_name(String su_name) {
		this.su_name = su_name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getJc_name() {
		return jc_name;
	}
	public void setJc_name(String jc_name) {
		this.jc_name = jc_name;
	}
	public String getJc_bh() {
		return jc_bh;
	}
	public void setJc_bh(String jc_bh) {
		this.jc_bh = jc_bh;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Long getJc_id() {
		return jc_id;
	}
	public void setJc_id(Long jc_id) {
		this.jc_id = jc_id;
	}
	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	@Override
	public String toString() {
		return "Cjid" + id +  "1type" + type + "1num" + num + "1jc_id" + jc_id + "1jc_bh" + jc_bh
				+ "1member_id" + member_id;
	}
	

}
