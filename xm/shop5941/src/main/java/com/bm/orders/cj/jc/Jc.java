package com.bm.orders.cj.jc;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Jc extends BaseEN{
	private Long id;
	private String bh;//编号
	private BigDecimal num;//中奖几率，0-10000，如果为0则不会抽中，如果是10000则必中
	private String title;//奖品描述
	private String title_su;//奖池描述
	private Long xn_id;//虚拟奖品id，如优惠价
	private Long star;//开始时间
	private Long end;//结束时间
	private Integer m_num;//所需积分
	private String imagepath;//奖品图片
	private Integer dqyz;//当前阈值，系统参数，不用管
	private String jiang_ctu;//奖池图片
	
	
	
	public String getJiang_ctu() {
		return jiang_ctu;
	}
	public void setJiang_ctu(String jiang_ctu) {
		this.jiang_ctu = jiang_ctu;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public Integer getDqyz() {
		return dqyz;
	}
	public void setDqyz(Integer dqyz) {
		this.dqyz = dqyz;
	}
	public Integer getM_num() {
		return m_num;
	}
	public void setM_num(Integer m_num) {
		this.m_num = m_num;
	}
	public String getTitle_su() {
		return title_su;
	}
	public void setTitle_su(String title_su) {
		this.title_su = title_su;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBh() {
		return bh;
	}
	public void setBh(String bh) {
		this.bh = bh;
	}
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getXn_id() {
		return xn_id;
	}
	public void setXn_id(Long xn_id) {
		this.xn_id = xn_id;
	}
	public Long getStar() {
		return star;
	}
	public void setStar(Long star) {
		this.star = star;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "Jcid" + id + "1bh" + bh + "1num" + num + "1title" + title + "1title_su" + title_su + "1xn_id" + xn_id
				+ "1star" + star + "1end" + end + "1m_num" + m_num + "1dqyz" + dqyz + "1imagepath" + imagepath;
	}
	
	
	

}
