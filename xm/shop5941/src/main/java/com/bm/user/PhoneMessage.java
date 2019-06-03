package com.bm.user;

import com.bm.base.BaseEN;

public class PhoneMessage extends BaseEN{
	private Long id;// is 'id'; ;
	private String phone;// is '手机号'; ;
	private Integer code;// is '验证码'; ;
	private String msg;// is '验证码内容'; ;
	private Long time;// is '发送时间'; ;
	private Integer status;// is '状态'; ;0:可用，1：已使用
	private Integer type;// is '验证码类型'; ;0:注册，1找回密码,2:修改绑定手机号
	
	
	@Override
	public String toString() {return"PhoneMessageid"+id+"phone"+phone+"code"+code+"msg"+msg+"time"+time+"status"+status+"type"+type;
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
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	

}
