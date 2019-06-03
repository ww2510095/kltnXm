package com.bm.wx.xuankuan.u;

import com.bm.base.BaseEN;

public class X_u extends BaseEN{

	private Long id;
	private String x_name;//账号
	private String x_pwd;//密码
	private Integer ex;//等级
	private String x_b;//备用字段
	private Integer x_t;//状态，0：正常，-1，已冻结
	private String x_m_name;//所属门店名字
	private String x_m_code;//所属门店编号
	
	private Long x_z_time;//最后使用时间
	
	
	public Long getX_z_time() {
		return x_z_time;
	}
	public void setX_z_time(Long x_z_time) {
		this.x_z_time = x_z_time;
	}
	public Integer getX_t() {
		return x_t;
	}
	public void setX_t(Integer x_t) {
		this.x_t = x_t;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getX_name() {
		return x_name;
	}
	public void setX_name(String x_name) {
		this.x_name = x_name;
	}
	public String getX_pwd() {
		return x_pwd;
	}
	public void setX_pwd(String x_pwd) {
		this.x_pwd = x_pwd;
	}
	public Integer getEx() {
		return ex;
	}
	public void setEx(Integer ex) {
		this.ex = ex;
	}
	public String getX_b() {
		return x_b;
	}
	public void setX_b(String x_b) {
		this.x_b = x_b;
	}
	public String getX_m_name() {
		return x_m_name;
	}
	public void setX_m_name(String x_m_name) {
		this.x_m_name = x_m_name;
	}
	public String getX_m_code() {
		return x_m_code;
	}
	public void setX_m_code(String x_m_code) {
		this.x_m_code = x_m_code;
	}
	@Override
	public String toString() {
		return "X_uid" + id + "1x_name" + x_name + "1x_pwd" + x_pwd + "1ex" + ex + "1x_b" + x_b + "1x_t" + x_t
				+ "1x_m_name" + x_m_name + "1x_m_code" + x_m_code;
	}
	
	

}
