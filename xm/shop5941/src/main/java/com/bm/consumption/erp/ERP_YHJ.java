package com.bm.consumption.erp;

import com.bm.base.BaseEN;

public class ERP_YHJ extends BaseEN{
	private int id;//
    private String coupon_sn;//优惠券编号
    private String coupon_config_sn;//优惠券类型编号
    private String coupon_price;//优惠券面额
    private String spend_amount ;//满x元可使用
    private int term_days ;//有期效天数
    private String rec_status ;//状态：A已生成；S已发送；F已使用,D已删除
    private String createtime;//创建时间
    private int creater ;//创建人id
    private String sendtime ;//发送时间
    private int send_shop ;//发送门店id
    private String send_bill_sn;//发送小票编号
    private String usetime;//使用时间
    private int use_shop ;//使用门店id
    private String use_bill_sn;//使用小票编号
    private String use_money ;//使用金额
    private String start_time;//开始时间
    private String end_time;//结束时间
    private String remark;//备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCoupon_sn() {
		return coupon_sn;
	}
	public void setCoupon_sn(String coupon_sn) {
		this.coupon_sn = coupon_sn;
	}
	public String getCoupon_config_sn() {
		return coupon_config_sn;
	}
	public void setCoupon_config_sn(String coupon_config_sn) {
		this.coupon_config_sn = coupon_config_sn;
	}
	public String getCoupon_price() {
		return coupon_price;
	}
	public void setCoupon_price(String coupon_price) {
		this.coupon_price = coupon_price;
	}
	public String getSpend_amount() {
		return spend_amount;
	}
	public void setSpend_amount(String spend_amount) {
		this.spend_amount = spend_amount;
	}
	public int getTerm_days() {
		return term_days;
	}
	public void setTerm_days(int term_days) {
		this.term_days = term_days;
	}
	public String getRec_status() {
		return rec_status;
	}
	public void setRec_status(String rec_status) {
		this.rec_status = rec_status;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public int getCreater() {
		return creater;
	}
	public void setCreater(int creater) {
		this.creater = creater;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public int getSend_shop() {
		return send_shop;
	}
	public void setSend_shop(int send_shop) {
		this.send_shop = send_shop;
	}
	public String getSend_bill_sn() {
		return send_bill_sn;
	}
	public void setSend_bill_sn(String send_bill_sn) {
		this.send_bill_sn = send_bill_sn;
	}
	public String getUsetime() {
		return usetime;
	}
	public void setUsetime(String usetime) {
		this.usetime = usetime;
	}
	public int getUse_shop() {
		return use_shop;
	}
	public void setUse_shop(int use_shop) {
		this.use_shop = use_shop;
	}
	public String getUse_bill_sn() {
		return use_bill_sn;
	}
	public void setUse_bill_sn(String use_bill_sn) {
		this.use_bill_sn = use_bill_sn;
	}
	public String getUse_money() {
		return use_money;
	}
	public void setUse_money(String use_money) {
		this.use_money = use_money;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "ERP_YHJid" + id + "1coupon_sn" + coupon_sn + "1coupon_config_sn" + coupon_config_sn + "1coupon_price"
				+ coupon_price + "1spend_amount" + spend_amount + "1term_days" + term_days + "1rec_status" + rec_status
				+ "1createtime" + createtime + "1creater" + creater + "1sendtime" + sendtime + "1send_shop" + send_shop
				+ "1send_bill_sn" + send_bill_sn + "1usetime" + usetime + "1use_shop" + use_shop + "1use_bill_sn"
				+ use_bill_sn + "1use_money" + use_money + "1start_time" + start_time + "1end_time" + end_time
				+ "1remark" + remark;
	}
	
    
    
    
}
