package com.bm.user;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Member extends BaseEN{

	private Long id;// 
	private String uname;// '用户名'; ;
	private String password;// '密码'; ;
	private Integer source;// '用户来源'; ;1:注册，2：管理员添加
	private Integer state;// '用户状态'; ;//1可用，2：禁用
	private Integer superadmin;// '是否是超级管理员'; ;//1：是
	private String phone;// '手机号'; ;
	private String portrait;// '头像'; ;
	private Integer sex;// '性别'; 0保密;1：男，2：女
	private Integer age;// '年龄'; ;
	private String nickname;// '昵称'; ;
	private String emil;// '邮箱'; ;
	private Long addtime;// '最后修改时间'; ;
	private BigDecimal platformcurrency;// '平台币（余额）'; ;
	private String usersystem;// 用户所属系统
	private String weiid;// 微信id
	private BigDecimal goldcoin;//积分
	
	private String zfb;//支付宝号
	private String membername;// 真实姓名
	
	private Integer membersize;// 登录次数
	
	
	
	
	
	
	
	
	
	
	public Integer getMembersize() {
		return membersize;
	}


	public void setMembersize(Integer membersize) {
		this.membersize = membersize;
	}


	@Override
	public String toString() {
		return "Memberid" + id + "1uname" + uname + "1password" + password + "1source" + source + "1state" + state
				+ "1superadmin" + superadmin + "1phone" + phone + "1portrait" + portrait + "1sex" + sex + "1age" + age
				+ "1nickname" + nickname + "1emil" + emil + "1addtime" + addtime + "1platformcurrency"
				+ platformcurrency + "1usersystem" + usersystem + "1weiid" + weiid + "1goldcoin" + goldcoin + "1zfb"
				+ zfb + "1membername" + membername;
	}


	public String getZfb() {
		return zfb;
	}


	public void setZfb(String zfb) {
		this.zfb = zfb;
	}


	public String getMembername() {
		return membername;
	}


	public void setMembername(String membername) {
		this.membername = membername;
	}


//	@Override
//	public String toString() {return"Memberid"+id+"uname"+uname+"password"+password+"source"+source+"state"+state+"superadmin"+superadmin+"phone"+phone+"emil"+emil+"addtime"+addtime+"portrait"+portrait+"sex"+sex+"age"+age+"platformcurrency"+platformcurrency+"nickname"+nickname+"usersystem"+usersystem+"goldcoin"+goldcoin;
//}
	
	
	public String getWeiid() {
		return weiid;
	}


	public void setWeiid(String weiid) {
		this.weiid = weiid;
	}


	public String getUsersystem() {
		return usersystem;
	}
	public void setUsersystem(String usersystem) {
		this.usersystem = usersystem;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getSuperadmin() {
		return superadmin;
	}
	public void setSuperadmin(Integer superadmin) {
		this.superadmin = superadmin;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmil() {
		return emil;
	}
	public void setEmil(String emil) {
		this.emil = emil;
	}
	public Long getAddtime() {
		return addtime;
	}
	public void setAddtime(Long addtime) {
		this.addtime = addtime;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public BigDecimal getPlatformcurrency() {
		return platformcurrency;
	}
	public void setPlatformcurrency(BigDecimal platformcurrency) {
		this.platformcurrency = platformcurrency;
	}
	public BigDecimal getGoldcoin() {
		return goldcoin;
	}
	public void setGoldcoin(BigDecimal goldcoin) {
		this.goldcoin = goldcoin;
	}
	
	

}
