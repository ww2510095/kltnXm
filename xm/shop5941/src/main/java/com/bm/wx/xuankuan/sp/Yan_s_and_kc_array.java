package com.bm.wx.xuankuan.sp;

public class Yan_s_and_kc_array {
	
	private String yan_s;
	private Integer zong_kc;//总库存
	private Integer dong_jkc;//冻结库存
	private Integer sheng_ykc;//剩余库存
	public String getYan_s() {
		return yan_s;
	}
	public void setYan_s(String yan_s) {
		this.yan_s = yan_s;
	}
	public Integer getZong_kc() {
		return zong_kc;
	}
	public void setZong_kc(Integer zong_kc) {
		this.zong_kc = zong_kc;
	}
	public Integer getDong_jkc() {
		return dong_jkc;
	}
	public void setDong_jkc(Integer dong_jkc) {
		this.dong_jkc = dong_jkc;
	}
	public Integer getSheng_ykc() {
		return sheng_ykc;
	}
	public void setSheng_ykc(Integer sheng_ykc) {
		this.sheng_ykc = sheng_ykc;
	}
	@Override
	public String toString() {
		return "Yan_s_and_kc_arrayyan_s" + yan_s + "1zong_kc" + zong_kc + "1dong_jkc" + dong_jkc + "1sheng_ykc"
				+ sheng_ykc;
	}


}
