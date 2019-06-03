package com.bm.orders.cj;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.redis.RedisUtils;
import com.bm.base.util.IBeanUtil;

@Service
public class CjService extends BaseService{

	@Override
	protected String getTabName() {
		return "cj";
	}
	
	public int add(Cj obj) throws Exception {
		if(obj.getNum()==null)obj.setNum(0);
		return super.add(obj);
	}
	@Override
	public List<?> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		Sql msql = new Sql();
		msql.setSql("select cj.*,nvl(jc.title,Coupon.title) jc_name from cj left join jc on jc_id=jc.id left join Coupon on Coupon.id=jc_id "
				+ "left join Cjaddress on jc.id=Cjaddress.jc_id" + getWhere(t));
		msql.setOrderbykey(orderbykey);
		msql.setOrderbytype(orderbytype);
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
	@Override
	public String getWhere(Object obj) throws Exception {
		if(obj==null) return "";
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		StringBuilder sb = new StringBuilder(" where 1=1 ");
		Field[] field = obj.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			MethodName = field[i].getName();
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
			m = obj.getClass().getMethod("get" + MethodName);
			value = m.invoke(obj);
			if (value != null&&value.toString().trim().length()!=0)
				sb.append("and cj." + MethodName + "='" + value + "' ");

		}
		return sb.toString();
	}
}
