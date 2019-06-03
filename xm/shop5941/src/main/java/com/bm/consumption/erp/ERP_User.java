package com.bm.consumption.erp;

public class ERP_User {
	private String update_datetime;
    private long ic_id;
    /// <summary>
    /// 注册时间
    /// </summary>
    private String ic_reg_time;
    private long ic_reg_user_id;
    private String shop_code;
    private String ic_act_time;
    private long ic_act_user_id;
    private String ic_sn;
    private String ic_type;
    /// <summary>
    /// 积分
    /// </summary>
    private String ic_integral;
    private String ic_cash_balance;
    private String ic_coupon_balance;
    private String ic_coupon_pvtime;
    private String ic_ini_balance;
    private String ic_pvtime;
    private long ic_pvtime_year;
    /// <summary>
    /// 卡状态
    /// </summary>
    private String ic_status;
    /// <summary>
    /// 卡号
    /// </summary>
    private String ic_code;
    private String card_code;
    /// <summary>
    /// 会员姓名
    /// </summary>
    private String vip_name;
    /// <summary>
    /// 性别
    /// </summary>
    private String vip_sex;
    /// <summary>
    /// 生日
    /// </summary>
    private String vip_birthday;
    /// <summary>
    /// 手机
    /// </summary>
    private String vip_phone;
    /// <summary>
    /// 备注
    /// </summary>
    private String vip_remark;
    private String ic_act_status;
    private String iscutover = "F";
    private int cutover_userid;
    private String cutoverdate;
    /// <summary>
    /// 邮箱
    /// </summary>
    private String vip_email;
    /// <summary>
    /// 地址
    /// </summary>
    private String vip_address;
    private String accept_sms = "T";
    /// <summary>
    /// 身份证
    /// </summary>
    private String identity_card_number;
    //private decimal integralMoney = 10.00M
	

	@Override
	public String toString() {
		return "ERP_Userupdate_datetime" + update_datetime + "1ic_id" + ic_id + "1ic_reg_time" + ic_reg_time
				+ "1ic_reg_user_id" + ic_reg_user_id + "1shop_code" + shop_code + "1ic_act_time" + ic_act_time
				+ "1ic_act_user_id" + ic_act_user_id + "1ic_sn" + ic_sn + "1ic_type" + ic_type + "1ic_integral"
				+ ic_integral + "1ic_cash_balance" + ic_cash_balance + "1ic_coupon_balance" + ic_coupon_balance
				+ "1ic_coupon_pvtime" + ic_coupon_pvtime + "1ic_ini_balance" + ic_ini_balance + "1ic_pvtime" + ic_pvtime
				+ "1ic_pvtime_year" + ic_pvtime_year + "1ic_status" + ic_status + "1ic_code" + ic_code + "1card_code"
				+ card_code + "1vip_name" + vip_name + "1vip_sex" + vip_sex + "1vip_birthday" + vip_birthday
				+ "1vip_phone" + vip_phone + "1vip_remark" + vip_remark + "1ic_act_status" + ic_act_status
				+ "1iscutover" + iscutover + "1cutover_userid" + cutover_userid + "1cutoverdate" + cutoverdate
				+ "1vip_email" + vip_email + "1vip_address" + vip_address + "1accept_sms" + accept_sms
				+ "1identity_card_number" + identity_card_number;
	}
	public String getUpdate_datetime() {
		return update_datetime;
	}
	public void setUpdate_datetime(String update_datetime) {
		this.update_datetime = update_datetime;
	}
	public long getIc_id() {
		return ic_id;
	}
	public void setIc_id(long ic_id) {
		this.ic_id = ic_id;
	}
	public String getIc_reg_time() {
		return ic_reg_time;
	}
	public void setIc_reg_time(String ic_reg_time) {
		this.ic_reg_time = ic_reg_time;
	}
	public long getIc_reg_user_id() {
		return ic_reg_user_id;
	}
	public void setIc_reg_user_id(long ic_reg_user_id) {
		this.ic_reg_user_id = ic_reg_user_id;
	}
	public String getShop_code() {
		return shop_code;
	}
	public void setShop_code(String shop_code) {
		this.shop_code = shop_code;
	}
	public String getIc_act_time() {
		return ic_act_time;
	}
	public void setIc_act_time(String ic_act_time) {
		this.ic_act_time = ic_act_time;
	}
	public long getIc_act_user_id() {
		return ic_act_user_id;
	}
	public void setIc_act_user_id(long ic_act_user_id) {
		this.ic_act_user_id = ic_act_user_id;
	}
	public String getIc_sn() {
		return ic_sn;
	}
	public void setIc_sn(String ic_sn) {
		this.ic_sn = ic_sn;
	}
	public String getIc_type() {
		return ic_type;
	}
	public void setIc_type(String ic_type) {
		this.ic_type = ic_type;
	}
	public String getIc_integral() {
		return ic_integral;
	}
	public void setIc_integral(String ic_integral) {
		this.ic_integral = ic_integral;
	}
	public String getIc_cash_balance() {
		return ic_cash_balance;
	}
	public void setIc_cash_balance(String ic_cash_balance) {
		this.ic_cash_balance = ic_cash_balance;
	}
	public String getIc_coupon_balance() {
		return ic_coupon_balance;
	}
	public void setIc_coupon_balance(String ic_coupon_balance) {
		this.ic_coupon_balance = ic_coupon_balance;
	}
	public String getIc_coupon_pvtime() {
		return ic_coupon_pvtime;
	}
	public void setIc_coupon_pvtime(String ic_coupon_pvtime) {
		this.ic_coupon_pvtime = ic_coupon_pvtime;
	}
	public String getIc_ini_balance() {
		return ic_ini_balance;
	}
	public void setIc_ini_balance(String ic_ini_balance) {
		this.ic_ini_balance = ic_ini_balance;
	}
	public String getIc_pvtime() {
		return ic_pvtime;
	}
	public void setIc_pvtime(String ic_pvtime) {
		this.ic_pvtime = ic_pvtime;
	}
	public long getIc_pvtime_year() {
		return ic_pvtime_year;
	}
	public void setIc_pvtime_year(long ic_pvtime_year) {
		this.ic_pvtime_year = ic_pvtime_year;
	}
	public String getIc_status() {
		return ic_status;
	}
	public void setIc_status(String ic_status) {
		this.ic_status = ic_status;
	}
	public String getIc_code() {
		return ic_code;
	}
	public void setIc_code(String ic_code) {
		this.ic_code = ic_code;
	}
	public String getCard_code() {
		return card_code;
	}
	public void setCard_code(String card_code) {
		this.card_code = card_code;
	}
	public String getVip_name() {
		return vip_name;
	}
	public void setVip_name(String vip_name) {
		this.vip_name = vip_name;
	}
	public String getVip_sex() {
		return vip_sex;
	}
	public void setVip_sex(String vip_sex) {
		this.vip_sex = vip_sex;
	}
	public String getVip_birthday() {
		return vip_birthday;
	}
	public void setVip_birthday(String vip_birthday) {
		this.vip_birthday = vip_birthday;
	}
	public String getVip_phone() {
		return vip_phone;
	}
	public void setVip_phone(String vip_phone) {
		this.vip_phone = vip_phone;
	}
	public String getVip_remark() {
		return vip_remark;
	}
	public void setVip_remark(String vip_remark) {
		this.vip_remark = vip_remark;
	}
	public String getIc_act_status() {
		return ic_act_status;
	}
	public void setIc_act_status(String ic_act_status) {
		this.ic_act_status = ic_act_status;
	}
	public String getIscutover() {
		return iscutover;
	}
	public void setIscutover(String iscutover) {
		this.iscutover = iscutover;
	}
	public int getCutover_userid() {
		return cutover_userid;
	}
	public void setCutover_userid(int cutover_userid) {
		this.cutover_userid = cutover_userid;
	}
	public String getCutoverdate() {
		return cutoverdate;
	}
	public void setCutoverdate(String cutoverdate) {
		this.cutoverdate = cutoverdate;
	}
	public String getVip_email() {
		return vip_email;
	}
	public void setVip_email(String vip_email) {
		this.vip_email = vip_email;
	}
	public String getVip_address() {
		return vip_address;
	}
	public void setVip_address(String vip_address) {
		this.vip_address = vip_address;
	}
	public String getAccept_sms() {
		return accept_sms;
	}
	public void setAccept_sms(String accept_sms) {
		this.accept_sms = accept_sms;
	}
	public String getIdentity_card_number() {
		return identity_card_number;
	}
	public void setIdentity_card_number(String identity_card_number) {
		this.identity_card_number = identity_card_number;
	}
	
}
