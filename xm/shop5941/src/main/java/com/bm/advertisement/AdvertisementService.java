package com.bm.advertisement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.IBeanUtil;

@Service
public class AdvertisementService extends BaseService{
	
	
	@SuppressWarnings("unchecked")
	public List<Advertisement> getALL(Advertisement obj, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		List<?> list = super.getALL(obj, orderbykey, orderbytype, page, rows);
		
		if(list==null||list.size()==0){
			if(!"4".equals(obj.getKey())){
				Advertisement mAdvertisement = (Advertisement) obj;
				mAdvertisement.setIstabledata(false);
				mAdvertisement.setPath("/upload/images/logo.png");
				List<Advertisement> lists = new ArrayList<Advertisement>();
				lists.add(mAdvertisement);
				return lists;
			}
			
		}
		return (List<Advertisement>) list;
	}

	@Override
	protected String getTabName() {
		return "Advertisement";
	}
	
	public int add(Advertisement mAdvertisement) throws Exception {
		mAdvertisement.setPath(mAdvertisement.getPath().replace(";", ""));
		return super.add(mAdvertisement);
	}

	
	public List<?> getALL(Advertisement mAdvertisement, Integer page, Integer rows,Integer type)
			throws Exception {
		Sql msql = new Sql();
		if(type!=null&&type==1)
			msql.setSql("select * from " + getTabName() + " " + getWhere(mAdvertisement));
		else
			msql.setSql("select * from " + getTabName() + " " + getWhere(mAdvertisement) + " and nvl(orderby,0) !=-1  ");
		msql.setOrderbykey("orderby");
		msql.setOrderbytype(1);
		msql.setPage(page);
		msql.setRows(rows);
		//先从缓存里面获取数据
		List<?> listmap =RedisUtils.getList(stringRedisTemplate, Redisinfo.getall, msql.toString(),mAdvertisement.getClass());
		if(listmap.size()==0){
			listmap = IBeanUtil.ListMap2ListJavaBean(mBaseDao.getALL(msql), mAdvertisement.getClass());
			RedisUtils.set(stringRedisTemplate, Redisinfo.getall, msql.toString(), listmap,Redistime);//
		}
		Integer size1 = rows==null?10:rows;
		for (Object object : listmap) {
			object.getClass().getMethod("setIstabledata",Boolean.class).invoke(object, size1==listmap.size());
		}
		return listmap ;
	}
}
