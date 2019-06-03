package com.bm.friends;

import com.bm.base.BaseEN;

public class Friends extends BaseEN{
	
	@Override
	public String toString() {return"Friendsid"+id+"memberida"+memberida+"memberidb"+memberidb+"memberidatype"+memberidatype+"type"+type+"bz"+bz;
}
	private Long id; //id
	private Long memberida; //邀请人id
	private Long memberidb; //被邀请人id
	private Integer memberidatype; //邀请人身份0：系统1：普通用户，2：商铺，3，店员
	private Integer type; //邀请方式1：扫码，2：普通注册，3：微信
	private String bz;//备注


	
	
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberida() {
		return memberida;
	}
	public void setMemberida(Long memberida) {
		this.memberida = memberida;
	}
	public Long getMemberidb() {
		return memberidb;
	}
	public void setMemberidb(Long memberidb) {
		this.memberidb = memberidb;
	}
	public Integer getMemberidatype() {
		return memberidatype;
	}
	public void setMemberidatype(Integer memberidatype) {
		this.memberidatype = memberidatype;
	}
	

	

}
