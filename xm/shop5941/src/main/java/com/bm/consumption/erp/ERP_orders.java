package com.bm.consumption.erp;


public class ERP_orders {
	
	private String id=null;
	private String Integral_Water_Remark ;
	private int User_ID ;
	private String Shop_Code ;
	/// <summary>
	/// 产生积分门店id
	/// </summary>        
	public int shop_id ;
	/// <summary>
	/// 产生积分小票
	/// </summary> 
	public String bill_sn ;
	/// <summary>
	/// 小票总金额
	/// </summary> 
	public String bill_val ="0";
	public String getIntegral_Water_Remark() {
		return Integral_Water_Remark;
	}
	public void setIntegral_Water_Remark(String integral_Water_Remark) {
		Integral_Water_Remark = integral_Water_Remark;
	}
	public int getUser_ID() {
		return User_ID;
	}
	public void setUser_ID(int user_ID) {
		User_ID = user_ID;
	}
	public String getShop_Code() {
		return Shop_Code;
	}
	public void setShop_Code(String shop_Code) {
		Shop_Code = shop_Code;
	}
	public int getShop_id() {
		return shop_id;
	}
	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}
	public String getBill_sn() {
		return bill_sn;
	}
	public void setBill_sn(String bill_sn) {
		this.bill_sn = bill_sn;
	}
	public String getBill_val() {
		return bill_val;
	}
	public void setBill_val(String bill_val) {
		this.bill_val = bill_val;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "ERP_orders [id=" + id + ", Integral_Water_Remark=" + Integral_Water_Remark + ", User_ID=" + User_ID
				+ ", Shop_Code=" + Shop_Code + ", shop_id=" + shop_id + ", bill_sn=" + bill_sn + ", bill_val="
				+ bill_val + "]";
	}
	
	
	



}
