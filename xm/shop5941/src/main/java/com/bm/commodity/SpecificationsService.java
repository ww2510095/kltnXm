package com.bm.commodity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.stereotype.Service;

import com.Shop5941Application;
import com.bm.base.BaseService;
import com.bm.base.Sql;
 
@Service
public class SpecificationsService extends BaseService{
	
	@Override
	public String getTabName() {return "Specifications";}
	
	public int addsu(Object obj) throws Exception {
		return super.add(obj);
	}
	/**
	 * 添加非空数据
	 */
	public int add(Object obj) throws Exception {
		if(obj.getClass().getMethod("getId").invoke(obj)==null){
			Long lid =System.currentTimeMillis();
			while (getById(lid)!=null) {
				lid = lid+1;
				
			}
			obj.getClass().getMethod("setId", Long.class).invoke(obj,lid);// 设置id
		}else{
			//obj.getClass().getMethod("setId", Long.class).invoke(obj,lid);// 设置id
		}
			
		String MethodName;// 方法名
		Method m;// 方法
		Object value;// 执行结果
		Field[] field = obj.getClass().getDeclaredFields(); // 拿到所有的字段值
		StringBuilder mkey = new StringBuilder();
		StringBuilder mvalue = new StringBuilder();
		int fieldlength = field.length;
		if (fieldlength == 0)
			return 0;// 空的javaben
		for (int i = 0; i < fieldlength; i++) {
			MethodName = field[i].getName();
			if(MethodName.toLowerCase().equals("vip")){
				mkey.append(MethodName);
				mkey.append(",");
				mvalue.append("(select nvl(max(vip),0)+1 from Commodity ),");
			
			}else{
				MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
				m = obj.getClass().getMethod("get" + MethodName);
				value = m.invoke(obj);
				if (value != null) {
					mkey.append(MethodName);
					mkey.append(",");
					mvalue.append("'");
					mvalue.append(value);
					mvalue.append("',");
				}
			}
			

		}
		String sql = "INSERT INTO " + getTabName() + " (" + mkey.substring(0, mkey.length() - 1) + ") " + "VALUES("
				+ mvalue.substring(0, mvalue.length() - 1) + ")";
		Shop5941Application.out("====================INSERT INTO============================");
		Shop5941Application.out("SQL:" + sql);
		Sql msql = new Sql();
		msql.setSql(sql);
		
		try {
			execSQL(msql,-1,"");
		} catch (Exception e) {
			obj.getClass().getMethod("setId", Long.class).invoke(obj,Long.valueOf(obj.getClass().getMethod("getId").invoke(obj).toString())+1 );// 设置id
			execSQL(msql,-1,"");
		}
		
		deleteRedisKey();
		
		return 1;
	}
	
   
}
