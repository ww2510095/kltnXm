package com.bm.wx.xuankuan.stock;

import java.util.List;

import com.bm.base.BaseEN;
import com.bm.wx.xuankuan.sp.Yan_s_and_kc_array;

public class XStock extends BaseEN{
	private Long id;
	private Long sp_id;//商品id
	private String shen_qkc;//申请库存
	private String gong_ysgykc;//实际给与库存
	private String shen_qr_name;//申请人账号
	private Integer shi_fty;//是否同意
	private Integer shi_fxg;//是否修改过库存
	private String bei_z;//备注
	private String suo_smdbh;//所属门店编号
	private String suo_smdmz;//所属门店名字
	
	private String sp_name;//商品id
	private List<Bz> bei_z_array;//备注数组
	private List<Yan_s_and_kc_array> mYan_s_and_kc_arrays;//单子数组
	
	
	


	public static class Shen_qkc_object{
		private String yan_s;

		public String getYan_s() {
			return yan_s;
		}

		public void setYan_s(String yan_s) {
			this.yan_s = yan_s;
		}
		
	}
	public Integer getShi_fxg() {
		return shi_fxg;
	}

	public void setShi_fxg(Integer shi_fxg) {
		this.shi_fxg = shi_fxg;
	}

	public List<Yan_s_and_kc_array> getmYan_s_and_kc_arrays() {
		return mYan_s_and_kc_arrays;
	}

	public void setmYan_s_and_kc_arrays(List<Yan_s_and_kc_array> mYan_s_and_kc_arrays) {
		this.mYan_s_and_kc_arrays = mYan_s_and_kc_arrays;
	}

	public String getSp_name() {
		return sp_name;
	}

	public void setSp_name(String sp_name) {
		this.sp_name = sp_name;
	}

	public List<Yan_s_and_kc_array> getMYan_s_and_kc_arrays() {
		return mYan_s_and_kc_arrays;
	}

	public void setMYan_s_and_kc_arrays(List<Yan_s_and_kc_array> mYan_s_and_kc_arrays) {
		this.mYan_s_and_kc_arrays = mYan_s_and_kc_arrays;
	}

	
	public String getSuo_smdmz() {
		return suo_smdmz;
	}

	public void setSuo_smdmz(String suo_smdmz) {
		this.suo_smdmz = suo_smdmz;
	}

	public String getSuo_smdbh() {
		return suo_smdbh;
	}

	public void setSuo_smdbh(String suo_smdbh) {
		this.suo_smdbh = suo_smdbh;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSp_id() {
		return sp_id;
	}

	public void setSp_id(Long sp_id) {
		this.sp_id = sp_id;
	}

	public String getShen_qkc() {
		return shen_qkc;
	}

	public void setShen_qkc(String shen_qkc) {
		this.shen_qkc = shen_qkc;
	}

	public String getGong_ysgykc() {
		return gong_ysgykc;
	}

	public void setGong_ysgykc(String gong_ysgykc) {
		this.gong_ysgykc = gong_ysgykc;
	}

	public String getShen_qr_name() {
		return shen_qr_name;
	}

	public void setShen_qr_name(String shen_qr_name) {
		this.shen_qr_name = shen_qr_name;
	}

	public Integer getShi_fty() {
		return shi_fty;
	}

	public void setShi_fty(Integer shi_fty) {
		this.shi_fty = shi_fty;
	}

	public String getBei_z() {
		return bei_z;
	}

	public void setBei_z(String bei_z) {
		this.bei_z = bei_z;
	}

	public List<Bz> getBei_z_array() {
		return bei_z_array;
	}

	public void setBei_z_array(List<Bz> bei_z_array) {
		this.bei_z_array = bei_z_array;
	}

	@Override
	public String toString() {
		return "XStockid" + id + "1sp_id" + sp_id + "1shen_qkc" + shen_qkc + "1gong_ysgykc" + gong_ysgykc
				+ "1shen_qr_name" + shen_qr_name + "1shi_fty" + shi_fty + "1bei_z" + bei_z + "1bei_z_array"
				+ bei_z_array;
	}
	
	
	
	

	
	
}
