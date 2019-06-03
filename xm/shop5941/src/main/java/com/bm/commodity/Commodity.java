package com.bm.commodity;

import java.math.BigDecimal;

import com.bm.base.BaseEN;
import com.myjar.Stringutil;
import lombok.Data;

/**
 * 商品类
 * @author Administrator
 *
 */
@Data
public class Commodity extends BaseEN{
	private Long id; //id
	private String mycode; //自己的条码
	private String youcode; //别人的条码
	private String largeclass; //大类
	private String inclass; //中类
	private String smallclass; //小类
	private String fineclass; //细类
	private String name; //商品名字
	private String supplier; //供应商
	private String brand; //品牌
	private String introduction; //商品简介
	private String detailed; //详细介绍
	private String company; //计量单位
	private String packingmethod; //包装方式
	private BigDecimal originalprice; //原价
	private BigDecimal price; //售价
	private BigDecimal costprice; //成本价
	private String colour; //颜色
	private String mysize; //尺码
	private String mainimage; //主图数组
	private String detailsimage; //详情图数组
	private String system; //所属系统
	private String specifications; //0：尺码，1：规格
	private String suppliername; //供应商店铺名字
	private String commoditykeyid; //商品id
	private String kc; //库存
	private Integer type; //状态：1正常，-1，已下架，-2已冻结
	private Integer vip; //推荐等级
	
	private String indexs; //模块名字
	private Integer indexsby; //模块排序
	
	private String code; //助记码
	private String manufactor; //厂家
	
	private Integer asize; //限购数量
	private Integer salesize; //销售数量
	private Integer ms; //是否在参与秒杀，0：否，1：是
	private Integer ys; //是否在参与预售，0：否，1：是
	private Integer pdd; //是否在参与平团，0：否，1：是
	private String bh; //拼团编号
	private Integer ptsl; //拼团数量
	private Integer ptfa; //拼团方案
	private String commoditykeytype; //自定义属性
	private String zdyhd; //自定义活动

	

	
//	public Integer getPtfa() {
//		return ptfa;
//	}
//	public void setPtfa(Integer ptfa) {
//		this.ptfa = ptfa;
//	}
//	public Integer getPtsl() {
//		return ptsl;
//	}
//	public void setPtsl(Integer ptsl) {
//		this.ptsl = ptsl;
//	}
//	public String getBh() {
//		return bh;
//	}
//	public void setBh(String bh) {
//		this.bh = bh;
//	}
//	public Integer getPdd() {
//		return pdd;
//	}
//	public void setPdd(Integer pdd) {
//		this.pdd = pdd;
//	}
//	public Integer getYs() {
//		return ys;
//	}
//	public void setYs(Integer ys) {
//		this.ys = ys;
//	}
//	public Integer getMs() {
//		return ms;
//	}
//	public void setMs(Integer ms) {
//		this.ms = ms;
//	}
//	public String getCommoditykeytype() {
//		return commoditykeytype;
//	}
//	public void setCommoditykeytype(String commoditykeytype) {
//		this.commoditykeytype = commoditykeytype;
//	}
//	public Integer getSalesize() {
//		return salesize;
//	}
//	public void setSalesize(Integer salesize) {
//		this.salesize = salesize;
//	}
//	public Integer getAsize() {
//		return asize;
//	}
//	public void setAsize(Integer asize) {
//		this.asize = asize;
//	}
//	public String getCode() {
//		return code;
//	}
//	public void setCode(String code) {
//		this.code = code;
//	}
//	public String getManufactor() {
//		return manufactor;
//	}
//	public void setManufactor(String manufactor) {
//		this.manufactor = manufactor;
//	}
//	public String getIndexs() {
//		return indexs;
//	}
//	public void setIndexs(String indexs) {
//		this.indexs = indexs;
//	}
//	public Integer getIndexsby() {
//		return indexsby;
//	}
//	public void setIndexsby(Integer indexsby) {
//		this.indexsby = indexsby;
//	}
//	public Integer getVip() {
//		return vip;
//	}
//	public void setVip(Integer vip) {
//		this.vip = vip;
//	}
//	public Integer getType() {
//		return type;
//	}
//	public void setType(Integer type) {
//		this.type = type;
//	}
	public void setType(String type) {
		if(Stringutil.isBlank(type))
			this.type=null;
		else
			this.type = Integer.parseInt(type);
	}
//	public String getKc() {
//		return kc;
//	}
//	public void setKc(String kc) {
//		this.kc = kc;
//	}
//	public String getCommoditykeyid() {
//		return commoditykeyid;
//	}
//	public void setCommoditykeyid(String commoditykeyid) {
//		this.commoditykeyid = commoditykeyid;
//	}
//	public String getSuppliername() {
//		return suppliername;
//	}
//	public void setSuppliername(String suppliername) {
//		this.suppliername = suppliername;
//	}
//	public String getSpecifications() {
//		return specifications;
//	}
//	public void setSpecifications(String specifications) {
//		this.specifications = specifications;
//	}
//	public String getSystem() {
//		return system;
//	}
//	public void setSystem(String system) {
//		this.system = system;
//	}
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getMycode() {
//		return mycode;
//	}
//	public void setMycode(String mycode) {
//		this.mycode = mycode;
//	}
//	public String getYoucode() {
//		return youcode;
//	}
//	public void setYoucode(String youcode) {
//		this.youcode = youcode;
//	}
//	public String getLargeclass() {
//		return largeclass;
//	}
//	public void setLargeclass(String largeclass) {
//		this.largeclass = largeclass;
//	}
//	public String getInclass() {
//		return inclass;
//	}
//	public void setInclass(String inclass) {
//		this.inclass = inclass;
//	}
//	public String getSmallclass() {
//		return smallclass;
//	}
//	public void setSmallclass(String smallclass) {
//		this.smallclass = smallclass;
//	}
//	public String getFineclass() {
//		return fineclass;
//	}
//	public void setFineclass(String fineclass) {
//		this.fineclass = fineclass;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getSupplier() {
//		return supplier;
//	}
//	public void setSupplier(String supplier) {
//		this.supplier = supplier;
//	}
//	public String getBrand() {
//		return brand;
//	}
//	public void setBrand(String brand) {
//		this.brand = brand;
//	}
//	public String getIntroduction() {
//		return introduction;
//	}
//	public void setIntroduction(String introduction) {
//		this.introduction = introduction;
//	}
//	public String getDetailed() {
//		return detailed;
//	}
//	public void setDetailed(String detailed) {
//		this.detailed = detailed;
//	}
//	public String getCompany() {
//		return company;
//	}
//	public void setCompany(String company) {
//		this.company = company;
//	}
//	public String getPackingmethod() {
//		return packingmethod;
//	}
//	public void setPackingmethod(String packingmethod) {
//		this.packingmethod = packingmethod;
//	}
//	public BigDecimal getOriginalprice() {
//		return originalprice;
//	}
//	public void setOriginalprice(BigDecimal originalprice) {
//		this.originalprice = originalprice;
//	}
//	public BigDecimal getPrice() {
//		return price;
//	}
//	public void setPrice(BigDecimal price) {
//		this.price = price;
//	}
//	public BigDecimal getCostprice() {
//		return costprice;
//	}
//	public void setCostprice(BigDecimal costprice) {
//		this.costprice = costprice;
//	}
//	public String getColour() {
//		return colour;
//	}
//	public void setColour(String colour) {
//		this.colour = colour;
//	}
//	public String getMysize() {
//		return mysize;
//	}
//	public void setMysize(String mysize) {
//		this.mysize = mysize;
//	}
//	public String getMainimage() {
//		return mainimage;
//	}
//	public void setMainimage(String mainimage) {
//		this.mainimage = mainimage;
//	}
//	public String getDetailsimage() {
//		return detailsimage;
//	}
//	public void setDetailsimage(String detailsimage) {
//		this.detailsimage = detailsimage;
//	}



	
	

}
	
	