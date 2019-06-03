package com.bm.wx.xuankuan.stock;

import com.Shop5941Application;
import com.bm.base.BaseService;
import com.bm.base.Sql;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
 
@Service
public class XstockService extends BaseService{
	
	@Override
	public String getTabName() {return "Xstock";}
	
	@Override
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
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());
			if(MethodName.equals("Mshen_qkc_object"))continue;
			m = obj.getClass().getMethod("get" + MethodName);
			value = m.invoke(obj);

			if (value != null) {
				mkey.append(MethodName);
				mkey.append(",");
				mvalue.append("'");
				String va = value.toString().trim();
				if(va.length()>0){
					if(va.substring(0,1).equals("？")||va.substring(0,1).equals("?")){
						va=va.substring(1,va.length());
					}
					if(va.substring(va.length()-1,va.length()).equals("？")||va.substring(va.length()-1,va.length()).equals("?")){
						va=va.substring(0,va.length()-1);
					}
				}
			
				mvalue.append(value.toString().trim());
				mvalue.append("',");
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
	

	
   
	/*public List<XStock> getALL(XStock mXStock, Integer page, Integer rows)
			throws Exception {
		@SuppressWarnings("unchecked")
		List<XStock> listXStock =(List<XStock>) super.getALL(mXStock,  page, rows);
		update_ys_array(listXStock);
		return listXStock;
	}*/
	
	/*public void update_ys_array(List<XStock> listXStock){
		for (XStock xStock : listXStock) {
			if(!Stringutil.isBlank(xStock.getBei_z())){
				String[] strs = xStock.getBei_z().split(";");
				List<Bz> listbz = new ArrayList<>();
				for (String string : strs) {
					String[] strs1 = string.split(":");
					Bz mBz = new Bz();
					mBz.setDate(MyDate.stampToDate(Long.valueOf(strs1[0])));
					mBz.setName(strs1[1]);
					mBz.setA(strs1[2]);
					listbz.add(mBz);
				}
				xStock.setBei_z_array(listbz);
				xStock.setBei_z(null);
			}
			xStock.setMYan_s_and_kc_arrays(GsonUtil.fromJsonList(xStock.getShen_qkc(), Yan_s_and_kc_array.class));
			xStock.setShen_qkc(null);
			xStock.setGong_ysgykc(null);
		}
	}*/
}
