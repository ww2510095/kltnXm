package com.bm.auths;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.IBeanUtil;
import com.bm.shop.Shop;
import com.bm.user.Member;
import com.myjar.desutil.RunException;

public class MemberAuths extends Member{
	
	private List<String> auths;//权限
	private Shop mShop;//店铺
	private boolean clerk;//是否是店员
	private Shop shopto;//绑定的店铺
	private String systemphone;//客服电话
//	private String Aphone;//推荐人电话
	
	private Integer m_type;
	
	
	

//	public String getAphone() {
//		return Aphone;
//	}
//
//	public void setAphone(String aphone) {
//		Aphone = aphone;
//	}

	public Integer getM_type() {
		return m_type;
	}

	public void setM_type(Integer m_type) {
		this.m_type = m_type;
	}

	public String getSystemphone() {
		return systemphone;
	}

	public void setSystemphone(String systemphone) {
		this.systemphone = systemphone;
	}

	public Shop getShopto(BaseService base,StringRedisTemplate stringRedisTemplate) {
		if(shopto!=null)return shopto;
		Sql msql = new Sql();
		//绑定的店铺
		msql.setSql("select * from shop where id = (select shopid from Clerk where memberid=(select memberida"
					+ " from Friends where memberidb="+getId()+"))");
		try {
			shopto = 	IBeanUtil.Map2JavaBean(base.exeSelectSql(msql).get(0), Shop.class);
		} catch (Exception e) {
			throw new RunException("用户"+getUname()+"门店信息拉取异常");
		}
		RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, getId(), this);
		return shopto;
	}

	public void setShopto(Shop shopto) {
		this.shopto = shopto;
	}

	public boolean isClerk() {
		return clerk;
	}

	public void setClerk(boolean clerk) {
		this.clerk = clerk;
	}

	public Shop getmShop() {
//		if(mShop==null)
//			throw new RunException(getUname()+"不是店铺");
		return mShop;
	}

	public void setmShop(Shop mShop) {
		this.mShop = mShop;
	}

	public List<String> getAuths() {
		return auths;
	}

	public void setAuths(List<String> auths) {
		this.auths = auths;
	}
	
	

}
