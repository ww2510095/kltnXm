package com.bm.shop;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.IBeanUtil;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@Service
public class ShopService extends BaseService{

	@Override
	protected String getTabName() {
		return "shop";
	}
	
	public int add(Shop mShop) throws Exception {
		if(Stringutil.isBlank(mShop.getCode()))
			mShop.setCode(System.currentTimeMillis()+"");
		
		if(getByparameter("memberid", mShop.getMemberid()+"")!=null)
			throw new RunException("错误,"+mShop.getShopuname()+"已经有店铺了");
		
		if(getByparameter("code", mShop.getCode())!=null)
			throw new RunException("错误,编号"+mShop.getCode()+"已经存在了");
		
		mShop.setShopphone(mShop.getShopuname());
		mShop.setShopuname(null);
		return super.add(mShop);
	}

	@Override
	public List<?> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		Sql msql = new Sql();
		msql.setSql("select shop.*,uname shopuname from " + getTabName() + " left join member on member.id=memberid " + getWhere(t));
		msql.setOrderbykey(orderbykey==null?"shop.id":orderbykey);
		msql.setOrderbytype(orderbykey==null?1:orderbytype);
		msql.setPage(page);
		msql.setRows(rows);
		//先从缓存里面获取数据
		List<?> listmap =RedisUtils.getList(stringRedisTemplate, Redisinfo.getall, msql.toString(),t.getClass());
		if(listmap.size()==0){
			listmap = IBeanUtil.ListMap2ListJavaBean(mBaseDao.getALL(msql), t.getClass());
			RedisUtils.set(stringRedisTemplate, Redisinfo.getall, msql.toString().replace(" ", ""), listmap,Redistime);//
		}
		Integer size1 = rows==null?10:rows;
		for (Object object : listmap) {
			object.getClass().getMethod("setIstabledata",Boolean.class).invoke(object, size1==listmap.size());
		}
		return listmap ;
	}
}
