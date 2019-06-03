package com.example.fw.base;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IBeanUtil {

	/**
	 * 将map《String,object》 格式转换为javabean,(此处的map的key必须是全大写，主要用户解析从数据库里面查询出来的结果集）
	 * clazz字节码文件必须提供空参构造方法
	 * @throws Exception 
	 * 
	 * */
	public static <T> T Map2JavaBean(Map<String, Object> map,Class<T> clazz) throws Exception {
		if(map==null||map.size()==0)return null;
		 String MethodName;//方法名
		 Object value;//map取出来的值
		 Field[] field = clazz.getDeclaredFields(); //拿到所有的字段值 
		 T obj = clazz.newInstance();
		 for (Field field2 : field) {
			 try {

				 MethodName = field2.getName();  
				 MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase()); 	
				 value = map.get(MethodName.toUpperCase());
				 if(value!=null){
					 try {
						 clazz.getMethod("set"+MethodName,value.getClass()).invoke(obj, value);//设置值
					} catch (Exception e) {
						 try {
							//数值型是BigDecimal
							 clazz.getMethod("set"+MethodName,BigDecimal.class).invoke(obj,new BigDecimal(value.toString()));//设置值
						} catch (Exception e1) {
							try {
								//数值型是int
								 clazz.getMethod("set"+MethodName,Integer.class).invoke(obj, (int)(double)Double.valueOf(value.toString()));//设置值
							} catch (Exception e2) {
								 try {
									//数值型是long
										clazz.getMethod("set"+MethodName,Long.class).invoke(obj, Long.valueOf(value.toString()));//设置值
									} catch (Exception e3) {
										//数值型是String
										clazz.getMethod("set"+MethodName,String.class).invoke(obj,value+"");//设置值
									}
								
							}
							
						}
					
					}
				 }
					
			 
			} catch (Exception e) {
				// 没有这个方法
			}
		 }
		return   obj;
	}
	
	/**
	 * 将map《String,object》 格式转换为javabean,(此处的map的key必须是全大写，主要用户解析从数据库里面查询出来的结果集）
	 * clazz字节码文件必须提供空参构造方法
	 * @throws Exception 
	 * */
	public static <T> List<T> ListMap2ListJavaBean(List<Map<String, Object>> lmap,Class<T> clazz) throws Exception {
		if(lmap.size()==0)return new ArrayList<T>();
		List<T> ltmap = new ArrayList<T>();
		for (Map<String, Object> map : lmap) {
			ltmap.add(Map2JavaBean(map, clazz));
		}
		return   ltmap;
	}
	/**
	 * 将javabean 格式转换为map
	 * clazz字节码文件必须提供空参构造方法
	 * @throws Exception 
	 * 
	 * */
	public static Map<String, Object>  JavaBean2Map(Object obj) throws Exception {
		Map<String, Object> mMap= new HashMap<String, Object>();
		if(obj==null)return mMap;
		
		Class<?> clazz=obj.getClass();
		String MethodName;//方法名
		Field[] field = clazz.getDeclaredFields(); //拿到所有的字段值 
		for (Field field2 : field) {
			MethodName = field2.getName();  
			MethodName = MethodName.replaceFirst(MethodName.substring(0, 1), MethodName.substring(0, 1).toUpperCase()); 	
			mMap.put(MethodName.toUpperCase(), clazz.getMethod("get"+MethodName,clazz).invoke(obj));
			
		}
		return   mMap;
	}
	
	/**
	 * javabean 格式转换为将map《String,object》,(此处的map的key必须是全大写，主要用户解析从数据库里面查询出来的结果集）
	 * clazz字节码文件必须提供空参构造方法
	 * @throws Exception 
	 * */
	public static  List<Map<String, Object>> ListJavaBean2ListMap(List<?> lobj) throws Exception {
		if(lobj.size()==0)return new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ltmap = new ArrayList<Map<String, Object>>();
		for (Object mObject : lobj) {
			ltmap.add(JavaBean2Map(mObject));
		}
		return   ltmap;
	}

}
