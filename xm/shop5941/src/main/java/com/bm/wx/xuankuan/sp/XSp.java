package com.bm.wx.xuankuan.sp;

import java.util.Arrays;

import com.bm.base.BaseEN;

public class XSp extends BaseEN{
	private Long id;
	private String da_l;//大类
	private String zhong_l;//中类
	private String xiao_l;//小类
	private String xi_l;//细类
	private String shang_pm;//商品名称
	private String zhu_jm;//助记码
	private String gong_ysbh;//供应商编号
	private String ping_p;//品牌
	private String nian_f;//年份
	private String ji_j;//季节
	private String ji_ldw;//计量单位
	private String can_pfg;//产品风格
	private String jia_gsx;//价格属性
	private String nian_ld;//年龄段
	private String bao_zfs;//包装方式
	private String ping_pj;//吊牌价
	private String cai_gj;//采购价
	private String ding_jqx;//定价权限
	private String shi_fyxsgzk;//是否允许手工折扣
	private String shang_srq;//上市日期
	private String xiao_szq;//销售周期
	private String yan_s;//颜色
	private String chi_m;//尺码
	private String zhi_xbz;//执行标准
	private String zhong_bbs;//中包标识
	private String wai_bfs;//外包方式
	private String nei_xs;//内箱数
	private String wai_xs;//外箱数
	private String cai_z;//材质
	private String ban_x;//版型
	private String hua_x;//花型
	private String ling_x;//领型
	private String feng_g;//风格
	private String lei_b;//类别
	private String bei_x;//杯型
	private String bei_m;//杯模
	private String ke_z;//克重
	private String yang_p;//样品
	private String zhu_tk;//主推款
	private String a1;//图片
	private String a2;//图片
	private String a3;//图片
	private String a4;//图片
	private Yan_s_and_kc_array[] yan_s_array;//颜色与库存数组
	
	
	/*private String zong_kc;//总库存
	private String dong_jkc;//冻结库存
	private String sheng_ykc;//剩余库存
*/	private Integer fa_b;//0:未发布，1：已发布
	
	private Long jie_ssj;//结束时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDa_l() {
		return da_l;
	}

	public void setDa_l(String da_l) {
		this.da_l = da_l;
	}

	public String getZhong_l() {
		return zhong_l;
	}

	public void setZhong_l(String zhong_l) {
		this.zhong_l = zhong_l;
	}

	public String getXiao_l() {
		return xiao_l;
	}

	public void setXiao_l(String xiao_l) {
		this.xiao_l = xiao_l;
	}

	public String getXi_l() {
		return xi_l;
	}

	public void setXi_l(String xi_l) {
		this.xi_l = xi_l;
	}

	public String getShang_pm() {
		return shang_pm;
	}

	public void setShang_pm(String shang_pm) {
		this.shang_pm = shang_pm;
	}

	public String getZhu_jm() {
		return zhu_jm;
	}

	public void setZhu_jm(String zhu_jm) {
		this.zhu_jm = zhu_jm;
	}

	public String getGong_ysbh() {
		return gong_ysbh;
	}

	public void setGong_ysbh(String gong_ysbh) {
		this.gong_ysbh = gong_ysbh;
	}

	public String getPing_p() {
		return ping_p;
	}

	public void setPing_p(String ping_p) {
		this.ping_p = ping_p;
	}

	public String getNian_f() {
		return nian_f;
	}

	public void setNian_f(String nian_f) {
		this.nian_f = nian_f;
	}

	public String getJi_j() {
		return ji_j;
	}

	public void setJi_j(String ji_j) {
		this.ji_j = ji_j;
	}

	public String getJi_ldw() {
		return ji_ldw;
	}

	public void setJi_ldw(String ji_ldw) {
		this.ji_ldw = ji_ldw;
	}

	public String getCan_pfg() {
		return can_pfg;
	}

	public void setCan_pfg(String can_pfg) {
		this.can_pfg = can_pfg;
	}

	public String getJia_gsx() {
		return jia_gsx;
	}

	public void setJia_gsx(String jia_gsx) {
		this.jia_gsx = jia_gsx;
	}

	public String getNian_ld() {
		return nian_ld;
	}

	public void setNian_ld(String nian_ld) {
		this.nian_ld = nian_ld;
	}

	public String getBao_zfs() {
		return bao_zfs;
	}

	public void setBao_zfs(String bao_zfs) {
		this.bao_zfs = bao_zfs;
	}

	public String getPing_pj() {
		return ping_pj;
	}

	public void setPing_pj(String ping_pj) {
		this.ping_pj = ping_pj;
	}

	public String getCai_gj() {
		return cai_gj;
	}

	public void setCai_gj(String cai_gj) {
		this.cai_gj = cai_gj;
	}

	public String getDing_jqx() {
		return ding_jqx;
	}

	public void setDing_jqx(String ding_jqx) {
		this.ding_jqx = ding_jqx;
	}

	public String getShi_fyxsgzk() {
		return shi_fyxsgzk;
	}

	public void setShi_fyxsgzk(String shi_fyxsgzk) {
		this.shi_fyxsgzk = shi_fyxsgzk;
	}

	public String getShang_srq() {
		return shang_srq;
	}

	public void setShang_srq(String shang_srq) {
		this.shang_srq = shang_srq;
	}

	public String getXiao_szq() {
		return xiao_szq;
	}

	public void setXiao_szq(String xiao_szq) {
		this.xiao_szq = xiao_szq;
	}

	public String getYan_s() {
		return yan_s;
	}

	public void setYan_s(String yan_s) {
		this.yan_s = yan_s;
	}

	public String getChi_m() {
		return chi_m;
	}

	public void setChi_m(String chi_m) {
		this.chi_m = chi_m;
	}

	public String getZhi_xbz() {
		return zhi_xbz;
	}

	public void setZhi_xbz(String zhi_xbz) {
		this.zhi_xbz = zhi_xbz;
	}

	public String getZhong_bbs() {
		return zhong_bbs;
	}

	public void setZhong_bbs(String zhong_bbs) {
		this.zhong_bbs = zhong_bbs;
	}

	public String getWai_bfs() {
		return wai_bfs;
	}

	public void setWai_bfs(String wai_bfs) {
		this.wai_bfs = wai_bfs;
	}

	public String getNei_xs() {
		return nei_xs;
	}

	public void setNei_xs(String nei_xs) {
		this.nei_xs = nei_xs;
	}

	public String getWai_xs() {
		return wai_xs;
	}

	public void setWai_xs(String wai_xs) {
		this.wai_xs = wai_xs;
	}

	public String getCai_z() {
		return cai_z;
	}

	public void setCai_z(String cai_z) {
		this.cai_z = cai_z;
	}

	public String getBan_x() {
		return ban_x;
	}

	public void setBan_x(String ban_x) {
		this.ban_x = ban_x;
	}

	public String getHua_x() {
		return hua_x;
	}

	public void setHua_x(String hua_x) {
		this.hua_x = hua_x;
	}

	public String getLing_x() {
		return ling_x;
	}

	public void setLing_x(String ling_x) {
		this.ling_x = ling_x;
	}

	public String getFeng_g() {
		return feng_g;
	}

	public void setFeng_g(String feng_g) {
		this.feng_g = feng_g;
	}

	public String getLei_b() {
		return lei_b;
	}

	public void setLei_b(String lei_b) {
		this.lei_b = lei_b;
	}

	public String getBei_x() {
		return bei_x;
	}

	public void setBei_x(String bei_x) {
		this.bei_x = bei_x;
	}

	public String getBei_m() {
		return bei_m;
	}

	public void setBei_m(String bei_m) {
		this.bei_m = bei_m;
	}

	public String getKe_z() {
		return ke_z;
	}

	public void setKe_z(String ke_z) {
		this.ke_z = ke_z;
	}

	public String getYang_p() {
		return yang_p;
	}

	public void setYang_p(String yang_p) {
		this.yang_p = yang_p;
	}

	public String getZhu_tk() {
		return zhu_tk;
	}

	public void setZhu_tk(String zhu_tk) {
		this.zhu_tk = zhu_tk;
	}

	public String getA1() {
		return a1;
	}

	public void setA1(String a1) {
		this.a1 = a1;
	}

	public String getA2() {
		return a2;
	}

	public void setA2(String a2) {
		this.a2 = a2;
	}

	public String getA3() {
		return a3;
	}

	public void setA3(String a3) {
		this.a3 = a3;
	}

	public String getA4() {
		return a4;
	}

	public void setA4(String a4) {
		this.a4 = a4;
	}

	public Yan_s_and_kc_array[] getYan_s_array() {
		return yan_s_array;
	}

	public void setYan_s_array(Yan_s_and_kc_array[] yan_s_array) {
		this.yan_s_array = yan_s_array;
	}

	public Integer getFa_b() {
		return fa_b;
	}

	public void setFa_b(Integer fa_b) {
		this.fa_b = fa_b;
	}

	public Long getJie_ssj() {
		return jie_ssj;
	}

	public void setJie_ssj(Long jie_ssj) {
		this.jie_ssj = jie_ssj;
	}

	@Override
	public String toString() {
		return "XSpid" + id + "1da_l" + da_l + "1zhong_l" + zhong_l + "1xiao_l" + xiao_l + "1xi_l" + xi_l + "1shang_pm"
				+ shang_pm + "1zhu_jm" + zhu_jm + "1gong_ysbh" + gong_ysbh + "1ping_p" + ping_p + "1nian_f" + nian_f
				+ "1ji_j" + ji_j + "1ji_ldw" + ji_ldw + "1can_pfg" + can_pfg + "1jia_gsx" + jia_gsx + "1nian_ld"
				+ nian_ld + "1bao_zfs" + bao_zfs + "1ping_pj" + ping_pj + "1cai_gj" + cai_gj + "1ding_jqx" + ding_jqx
				+ "1shi_fyxsgzk" + shi_fyxsgzk + "1shang_srq" + shang_srq + "1xiao_szq" + xiao_szq + "1yan_s" + yan_s
				+ "1chi_m" + chi_m + "1zhi_xbz" + zhi_xbz + "1zhong_bbs" + zhong_bbs + "1wai_bfs" + wai_bfs + "1nei_xs"
				+ nei_xs + "1wai_xs" + wai_xs + "1cai_z" + cai_z + "1ban_x" + ban_x + "1hua_x" + hua_x + "1ling_x"
				+ ling_x + "1feng_g" + feng_g + "1lei_b" + lei_b + "1bei_x" + bei_x + "1bei_m" + bei_m + "1ke_z" + ke_z
				+ "1yang_p" + yang_p + "1zhu_tk" + zhu_tk + "1a1" + a1 + "1a2" + a2 + "1a3" + a3 + "1a4" + a4
				+ "1yan_s_array" + Arrays.toString(yan_s_array) + "1fa_b" + fa_b + "1jie_ssj" + jie_ssj;
	}
	
	
	
	


}
