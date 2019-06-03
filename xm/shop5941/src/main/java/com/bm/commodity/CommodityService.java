package com.bm.commodity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bm.base.BaseService;
import com.bm.base.Sql;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;
 
@Service
public class CommodityService extends BaseService{
	
	@Override
	public String getTabName() {return "commoditykey";}
	
	
	private enum deletesp{
	
		vip,originalprice,price,costprice,colour,mysize
	}
	@Transactional
	public void updateBySelect(Commodity mCommodity,String key,String keyvalue) throws Exception {
		if(Stringutil.isBlank(key)){
			key=" id";
			keyvalue=mCommodity.getId().toString();
		}
//		if(Stringutil.isBlank(mCommodity.getYoucode())){
//			mCommodity.setId(null);
			String MethodName;// 方法名
			Method m;// 方法
			Object value;// 执行结果
			Field[] field = mCommodity.getClass().getDeclaredFields(); // 拿到所有的字段值
			StringBuilder sql = new StringBuilder("update " + getTabName() + " set ");
			int fieldlength = field.length;
			if (fieldlength == 0)
				return;// 空的javaben
			for (int i = 0; i < fieldlength; i++) {
				MethodName = field[i].getName();
				Boolean ba =false;
				for (deletesp e : deletesp.values()) {
 					if(e.toString().equals(MethodName.toLowerCase())){
 						ba=true;
 						break;
 					}
					
				}
				if(ba)continue;
				MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase());

				m = mCommodity.getClass().getMethod("get" + MethodName);
				value = m.invoke(mCommodity);
				
				if ("删除".equals(value)){
					sql.append(MethodName + "='', ");// 拼接值不空sql
					 mCommodity.getClass().getMethod("set" + MethodName,String.class).invoke(mCommodity, "");
				}
				else if (value != null && !Stringutil.isBlank(value.toString()))
					sql.append(MethodName + "='" + value.toString() + "', ");// 拼接值不空sql
				
				

			
			}
			sql = new StringBuilder(sql.toString().trim().substring(0, sql.toString().trim().length() - 1));

			sql.append(" where "+key+" ='" + keyvalue + "'");
			Sql msql = new Sql();
			msql.setSql(sql.toString());
			
			//修改数据
			execSQL(msql,0,mCommodity.getYoucode());
			//清除缓存
			deleteRedisKey();
		

		
//		}
		if(mCommodity.getVip()!=null){
			int a = mCommodity.getVip();
//			Sql msql = new Sql();
			msql.setSql("update SPECIFICATIONS set vip="+a+"  where commoditykeyid="+mCommodity.getId());
			execSQL(msql, 0, a+"");
			mCommodity.setVip(null);
		}
		
		{
			BigDecimal s1 = mCommodity.getOriginalprice();mCommodity.setOriginalprice(null);
			BigDecimal s2 = mCommodity.getPrice();mCommodity.setPrice(null);
			BigDecimal s3 = mCommodity.getCostprice();mCommodity.setCostprice(null);
			String str = mCommodity.getColour();mCommodity.setColour(null);
			String str1 = mCommodity.getMysize();mCommodity.setMysize(null);
			if(!(s1==null&&s2==null&&s3==null&&Stringutil.isBlank(str)&&Stringutil.isBlank(str1))){
				String sql1 = "update SPECIFICATIONS set ";
				if(s1!=null)sql1=sql1+"Originalprice="+s1+", ";
				if(s2!=null)sql1=sql1+"Price="+s2+", ";
				if(s3!=null)sql1=sql1+"Costprice="+s3+", ";
				if(!Stringutil.isBlank(str))sql1=sql1+"Colour='"+str+"', ";
				if(!Stringutil.isBlank(str1))sql1=sql1+"Mysize='"+str1+"', ";
//				Sql msql = new Sql();
				if(Stringutil.isBlank(mCommodity.getYoucode()))
					msql.setSql(sql1.substring(0,sql1.length()-2)+" where commoditykeyid="+mCommodity.getId());
				else
					msql.setSql(sql1.substring(0,sql1.length()-2)+" where youcode="+mCommodity.getCode());
				execSQL(msql, 0, "");
			}
			
			
			
		}
		try {
			super.updateBySelect(mCommodity);
		} catch (Exception e) {
		}
			
	}
	@Transactional
	public void updateBySelect(Commodity mCommodity) throws Exception {
		updateBySelect(mCommodity,null,null);
	}

		@Override
		public Map<String, Object> getById(Object id) throws Exception {
			if (id instanceof String || id instanceof Integer || id instanceof Long)
				return mBaseDao.getById("commodity", id.toString());
			return mBaseDao.getById("commodity", id.getClass().getMethod("getId").invoke(id).toString());
		}
	
	@Transactional
	public int add(Commodity obj,int i) throws Exception {
	if(obj.getMainimage()==null&&obj.getMainimage().length()>2)
		if(obj.getMainimage().substring(obj.getMainimage().length()-1, obj.getMainimage().length()).equals(";"))
			obj.setMainimage(obj.getMainimage().substring(0,obj.getMainimage().length()-1));
		
	if(obj.getDetailsimage()!=null&&obj.getDetailsimage().length()>2)
		if(obj.getDetailsimage().substring(obj.getDetailsimage().length()-1, obj.getDetailsimage().length()).equals(";"))
			obj.setDetailsimage(obj.getDetailsimage().substring(0,obj.getDetailsimage().length()-1));
		
		Long str  =System.currentTimeMillis();
//		if(obj.getYoucode()==null)
//			obj.setYoucode(str);;
		if(obj.getMycode()==null)
			obj.setMycode(str+"");
		
		Sql msql = new Sql();
//		msql.setSql("select describe from Advertisement where describe='"+obj.getLargeclass()+"'");
//		
//		List<Map<String, Object>> listmap = exeSelectSql(msql);
//		if(listmap.size()==0){
//			msql.setSql("INSERT INTO Advertisement (id,describe,key) values("+str+",'"+obj.getLargeclass()+"',-1)");
//			exeSql(msql);
//		}

		String maxclass = obj.getLargeclass();
		if(maxclass==null||Stringutil.isBlank(maxclass.toString()))
			throw new RunException("第"+i+"行存在错误，大类是空的");
		Object minclass = obj.getFineclass();
		if(minclass==null||Stringutil.isBlank(minclass.toString()))
			throw new RunException("第"+i+"行存在错误，细类是空的");
		
		
		msql.setSql("select describe from Advertisement where describe='"+minclass.toString()+"' and bys='"+maxclass.toString()+"'");
		List<Map<String, Object>> listmap =exeSelectSql(msql);
		if(listmap.size()==0)
			throw new RunException("第"+i+"行存在错误，未找到对应类型");
	
		obj.setName(obj.getName().trim());
		if(obj.getId()==null){
			obj.setId(str);
			str++;
		}
		obj.setIndexs("新品推荐");
		obj.setSalesize(new Random().nextInt(99-10+1)+10);
		return super.add(obj);
	}
	@Override
	public List<Map<String, Object>> getALL(Object t, String orderbykey, Integer orderbytype, Integer page, Integer rows)
			throws Exception {
		Sql msql = new Sql();
		if("1".equals(t.getClass().getMethod("getType").invoke(t)+"")){
			msql.setOrderbykey("max(vip)");
			msql.setOrderbytype(1);
			msql.setSql("select min(nvl(ms.price,b.price)) price,max(my_null(ms.price))ms,max(my_null(pdd.price))pdd,max(my_null(ys.price))ys, originalprice,  LARGECLASS, INCLASS, commoditykeyid,"
					+ " SMALLCLASS,  FINECLASS, NAME, SUPPLIER, BRAND, INTRODUCTION, "
					+ " DETAILED, COMPANY,  PACKINGMETHOD,  MAINIMAGE, DETAILSIMAGE, "
					+ " SYSTEM, SPECIFICATIONS, SUPPLIERNAME,zdyhd from commoditykey a "
					+ " left join SPECIFICATIONS b on  b.commoditykeyid=a.id "
					+ "left join  Stock  on Stock.code=b.youcode "
					+ " left join (select num,code,price from ms where num>0 and end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))ms"
					+ " on ms.code=b.youcode "
					+ " left join (select num,code,price from ys where num>0 and end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))ys "
					+ " on Ys.code=b.youcode"
					+"  left join (select num,code,price,bh,type tfa from pdd where num>0 "
					+ " and star<(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + "
					+ "TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF'))  and "
					+ "end>(SYSDATE - TO_DATE('1970-1-1 8', 'YYYY-MM-DD HH24')) * 86400000 + TO_NUMBER(TO_CHAR(SYSTIMESTAMP(3), 'FF')))pdd"
					 +"  on pdd.code=b.youcode"
					+ " "+ getWhere(t)
					+ " group by  originalprice,  LARGECLASS, INCLASS, commoditykeyid, SMALLCLASS,  FINECLASS, NAME, SUPPLIER, BRAND, INTRODUCTION,  DETAILED, COMPANY,  PACKINGMETHOD,  MAINIMAGE, DETAILSIMAGE,  SYSTEM, SPECIFICATIONS, SUPPLIERNAME,zdyhd");
			}else {
				if("2".equals(t.getClass().getMethod("getType").invoke(t)+""))
					t.getClass().getMethod("setType",Integer.class).invoke(t,1);
			if("-1".equals(t.getClass().getMethod("getType").invoke(t)+"")){
				t.getClass().getMethod("setType",String.class).invoke(t,"");
				msql.setSql("select * from commodity" + getWhere(t)+" and type !=1");
			}
			else
				msql.setSql("select * from commodity " + getWhere(t) );
			
			msql.setOrderbykey(orderbykey);
			msql.setOrderbytype(orderbytype);
			}
		
		msql.setPage(page);
		msql.setRows(rows);
		return exeSelectSql(msql);
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
			if (value != null&&value.toString().trim().length()!=0){
				if(MethodName.toLowerCase().equals("fineclass")){
					sb.append("and (" + MethodName + "='" + value + "' or my_type(commoditykeytype,'"+value+"')=1)");
				}else{
					sb.append("and " + MethodName + "='" + value + "' ");
				}
				
			}
				
				
		}
		return sb.toString();
	}
	@Override
	@Transactional
	public int addList(List<?> listdata) throws Exception {
		long index =System.currentTimeMillis();
		Sql msql = new Sql();
		for (Object object : listdata){
			if(object.getClass().getMethod("getId").invoke(object)==null)
				object.getClass().getMethod("setId", Long.class).invoke(object, index);
			Object maxclass = object.getClass().getMethod("getLargeclass").invoke(object);
			if(maxclass==null||Stringutil.isBlank(maxclass.toString()))
				throw new RunException("错误，有大类是空的");
			Object minclass = object.getClass().getMethod("getFineclass").invoke(object);
			if(minclass==null||Stringutil.isBlank(minclass.toString()))
				throw new RunException("错误，有细类是空的");
			
			
			msql.setSql("select describe from Advertisement where describe='"+minclass.toString()+"' and key='"+maxclass.toString()+"'");
			List<Map<String, Object>> listmap =exeSelectSql(msql);
			if(listmap.size()==0)
				throw new RunException("错误,未找到大类为("+maxclass.toString()+"),细类为("+minclass.toString()+")的类型");
			index++;
			add(object);
		}
		return listdata.size();
	}
	@Override
	public void deleteByid(Object id) throws Exception {

		if (id instanceof String || id instanceof Integer || id instanceof Long){
			mBaseDao.deleteByid("SPECIFICATIONS", id.toString());
			//清除缓存
			deleteRedisKey();
			return ;
		}
			
		mBaseDao.deleteByid("SPECIFICATIONS", id.getClass().getMethod("getId").invoke(id).toString());
		//清除缓存
		deleteRedisKey();
	
	}
   
}
