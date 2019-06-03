package com.bm.coupon;

import java.math.BigDecimal;

import com.bm.base.BaseEN;

public class Coupon extends BaseEN{
	
	private Long id; //id
	private String title; //说明
	private BigDecimal numbera; //面值，如果小于1，则是打折
	private BigDecimal numbermin; //满多少使用
	private Long star; //开始时间
	private Long end; //结束时间
	private Integer type; //使用类型，1：通用卷，2：互斥卷
	private Integer state; //状态，0：未使用，1：已使用
	private Integer cardtype; //卷类型：1：单品，2：细类，3：小类，4：中类，5：大类,6全部
	private Integer key; //单品类型，1：一个商品，2：一款商品
	private String data; //条码或类名
	private Long memberid; //所属人
	private String mphone; //发放人
	private String onephone; //所属供应商
	
	
	//非字段
	 
	private String star1;
	private String end1;
	
	
	
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardtype == null) ? 0 : cardtype.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((end1 == null) ? 0 : end1.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((memberid == null) ? 0 : memberid.hashCode());
		result = prime * result + ((mphone == null) ? 0 : mphone.hashCode());
		result = prime * result + ((numbera == null) ? 0 : numbera.hashCode());
		result = prime * result + ((numbermin == null) ? 0 : numbermin.hashCode());
		result = prime * result + ((onephone == null) ? 0 : onephone.hashCode());
		result = prime * result + ((star == null) ? 0 : star.hashCode());
		result = prime * result + ((star1 == null) ? 0 : star1.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coupon other = (Coupon) obj;
		if (cardtype == null) {
			if (other.cardtype != null)
				return false;
		} else if (!cardtype.equals(other.cardtype))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (end1 == null) {
			if (other.end1 != null)
				return false;
		} else if (!end1.equals(other.end1))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (memberid == null) {
			if (other.memberid != null)
				return false;
		} else if (!memberid.equals(other.memberid))
			return false;
		if (mphone == null) {
			if (other.mphone != null)
				return false;
		} else if (!mphone.equals(other.mphone))
			return false;
		if (numbera == null) {
			if (other.numbera != null)
				return false;
		} else if (!numbera.equals(other.numbera))
			return false;
		if (numbermin == null) {
			if (other.numbermin != null)
				return false;
		} else if (!numbermin.equals(other.numbermin))
			return false;
		if (onephone == null) {
			if (other.onephone != null)
				return false;
		} else if (!onephone.equals(other.onephone))
			return false;
		if (star == null) {
			if (other.star != null)
				return false;
		} else if (!star.equals(other.star))
			return false;
		if (star1 == null) {
			if (other.star1 != null)
				return false;
		} else if (!star1.equals(other.star1))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	public String getOnephone() {
		return onephone;
	}
	public void setOnephone(String onephone) {
		this.onephone = onephone;
	}
	public String getStar1() {
		return star1;
	}
	public void setStar1(String star1) {
		this.star1 = star1;
	}
	public String getEnd1() {
		return end1;
	}
	public void setEnd1(String end1) {
		this.end1 = end1;
	}
	public String getMphone() {
		return mphone;
	}
	public void setMphone(String mphone) {
		this.mphone = mphone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BigDecimal getNumbera() {
		return numbera;
	}
	public void setNumbera(BigDecimal numbera) {
		this.numbera = numbera;
	}
	public BigDecimal getNumbermin() {
		return numbermin;
	}
	public void setNumbermin(BigDecimal numbermin) {
		this.numbermin = numbermin;
	}
	public Long getStar() {
		return star;
	}
	public void setStar(Long star) {
		this.star = star;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getCardtype() {
		return cardtype;
	}
	public void setCardtype(Integer cardtype) {
		this.cardtype = cardtype;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	@Override
	public String toString() {
		return "Couponid" + id + "1title" + title + "1numbera" + numbera + "1numbermin" + numbermin + "1star" + star
				+ "1end" + end + "1type" + type + "1state" + state + "1cardtype" + cardtype + "1key" + key + "1data"
				+ data + "1memberid" + memberid;
	}

}
