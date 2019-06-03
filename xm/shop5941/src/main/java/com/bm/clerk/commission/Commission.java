package com.bm.clerk.commission;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Commission extends BaseEN{
	private Long id;
	private Long ordersid;//订单id
	private Long memberid;//购买用户id
	private Long memberidsu;//分钱的人
	private BigDecimal num1;//导购分得钱
	private BigDecimal num2;//线上店主分的钱
	private BigDecimal num3;//经销商分的钱
	private BigDecimal num4;//实体店主分的钱
	private Integer type;//购买人身份，0，普通用户，1：普通导购，2：线上店主，3：分销商，4：店主
	private Integer typesu;//分钱人的身份1：普通导购，2：线上店主，3：分销商，4：店主：
	private Long fid;//分成方案id
	private Long memberidsusu;//分钱人上一级的人
	private BigDecimal numsu1;//为经销商贡献了多少钱
	private BigDecimal numsu2;//为门店店主贡献了了多少钱
	private Integer typenum;//金额状态，1：正常，2：冻结
	private Integer jtype;//冻结的节点，1：线上店主，2经销商，3，两个节点都冻结
	private BigDecimal ordersnum;//订单金额
	private BigDecimal onenum;//供应商分的金额
	private String memberone;//对应供应商
	private Long memberid1;//对应导购
	private Long memberid2;//对应线上店主
	private Long memberid3;//对应经销商分
	private Long memberid4;//对应实体店主
	private Integer state;//金额状态，1：未入账，2：已入账，3:已结算
	private Long time1;//结算时间
	private Long time2;//打款时间
	private Long time3;//打款处理时间
	private Integer commoditynum;//商品数量
	
	private Integer orderstype;//订单类型1:普通订单，2：核销卷
	
	
	
	public Integer getOrderstype() {
		return orderstype;
	}
	public void setOrderstype(Integer orderstype) {
		this.orderstype = orderstype;
	}
	public String getMemberone() {
		return memberone;
	}
	public void setMemberone(String memberone) {
		this.memberone = memberone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrdersid() {
		return ordersid;
	}
	public void setOrdersid(Long ordersid) {
		this.ordersid = ordersid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getMemberidsu() {
		return memberidsu;
	}
	public void setMemberidsu(Long memberidsu) {
		this.memberidsu = memberidsu;
	}
	public BigDecimal getNum1() {
		return num1;
	}
	public void setNum1(BigDecimal num1) {
		this.num1 = num1;
	}
	public BigDecimal getNum2() {
		return num2;
	}
	public void setNum2(BigDecimal num2) {
		this.num2 = num2;
	}
	public BigDecimal getNum3() {
		return num3;
	}
	public void setNum3(BigDecimal num3) {
		this.num3 = num3;
	}
	public BigDecimal getNum4() {
		return num4;
	}
	public void setNum4(BigDecimal num4) {
		this.num4 = num4;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getTypesu() {
		return typesu;
	}
	public void setTypesu(Integer typesu) {
		this.typesu = typesu;
	}
	public Long getFid() {
		return fid;
	}
	public void setFid(Long fid) {
		this.fid = fid;
	}
	public Long getMemberidsusu() {
		return memberidsusu;
	}
	public void setMemberidsusu(Long memberidsusu) {
		this.memberidsusu = memberidsusu;
	}
	public BigDecimal getNumsu1() {
		return numsu1;
	}
	public void setNumsu1(BigDecimal numsu1) {
		this.numsu1 = numsu1;
	}
	public BigDecimal getNumsu2() {
		return numsu2;
	}
	public void setNumsu2(BigDecimal numsu2) {
		this.numsu2 = numsu2;
	}
	public Integer getTypenum() {
		return typenum;
	}
	public void setTypenum(Integer typenum) {
		this.typenum = typenum;
	}
	public Integer getJtype() {
		return jtype;
	}
	public void setJtype(Integer jtype) {
		this.jtype = jtype;
	}
	public BigDecimal getOrdersnum() {
		return ordersnum;
	}
	public void setOrdersnum(BigDecimal ordersnum) {
		this.ordersnum = ordersnum;
	}
	public BigDecimal getOnenum() {
		return onenum;
	}
	public void setOnenum(BigDecimal onenum) {
		this.onenum = onenum;
	}
	public Long getMemberid1() {
		return memberid1;
	}
	public void setMemberid1(Long memberid1) {
		this.memberid1 = memberid1;
	}
	public Long getMemberid2() {
		return memberid2;
	}
	public void setMemberid2(Long memberid2) {
		this.memberid2 = memberid2;
	}
	public Long getMemberid3() {
		return memberid3;
	}
	public void setMemberid3(Long memberid3) {
		this.memberid3 = memberid3;
	}
	public Long getMemberid4() {
		return memberid4;
	}
	public void setMemberid4(Long memberid4) {
		this.memberid4 = memberid4;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Long getTime1() {
		return time1;
	}
	public void setTime1(Long time1) {
		this.time1 = time1;
	}
	public Long getTime2() {
		return time2;
	}
	public void setTime2(Long time2) {
		this.time2 = time2;
	}
	public Long getTime3() {
		return time3;
	}
	public void setTime3(Long time3) {
		this.time3 = time3;
	}
	public Integer getCommoditynum() {
		return commoditynum;
	}
	public void setCommoditynum(Integer commoditynum) {
		this.commoditynum = commoditynum;
	}
	@Override
	public String toString() {
		return "Commissionid" + id + "1ordersid" + ordersid + "1memberid" + memberid + "1memberidsu" + memberidsu
				+ "1num1" + num1 + "1num2" + num2 + "1num3" + num3 + "1num4" + num4 + "1type" + type + "1typesu"
				+ typesu + "1fid" + fid + "1memberidsusu" + memberidsusu + "1numsu1" + numsu1 + "1numsu2" + numsu2
				+ "1typenum" + typenum + "1jtype" + jtype + "1ordersnum" + ordersnum + "1onenum" + onenum
				+ "1memberidone" + memberone + "1memberid1" + memberid1 + "1memberid2" + memberid2 + "1memberid3"
				+ memberid3 + "1memberid4" + memberid4 + "1state" + state + "1time1" + time1 + "1time2" + time2
				+ "1time3" + time3 + "1commoditynum" + commoditynum;
	}



}
