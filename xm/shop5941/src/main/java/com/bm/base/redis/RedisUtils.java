package com.bm.base.redis;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bm.auths.MemberAuths;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.interceptor.InterceptorConfig;
import com.bm.base.util.GsonUtil;
import com.myjar.Stringutil;


public class RedisUtils {

	


    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,final Redisinfo redisinfo,final Object key, final Object value) {
    	
    	if(redisinfo==Redisinfo.redis_member_login)
    		 InterceptorConfig.lmap.put(key.toString(),(RedisMemberType)value);
    	try {
    		if(redisinfo==Redisinfo.redis_member_user)
        		InterceptorConfig.umap.put(Long.valueOf(key.toString()),(MemberAuths)value);
		} catch (Exception e) {
		}
    	
    	stringRedisTemplate.opsForValue().set(getkey(redisinfo, key), GsonUtil.toJsonString(value));
    	}
    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,final Redisinfo redisinfo,final Object key, final Object value,Long time) {
    	stringRedisTemplate.opsForValue().set(getkey(redisinfo, key), GsonUtil.toJsonString(value),time);
    }
    
    /**
     * 删除缓存
     * @param key
     * @param value
     * @return
     */
    public static void delete(final StringRedisTemplate stringRedisTemplate,final String key) {
    	if(Stringutil.isBlank(key))return;
    	stringRedisTemplate.delete(key);
    }
    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,final Redisinfo redisinfo,final Object key, List<?> listmap) {
		set(stringRedisTemplate, redisinfo, key, GsonUtil.toJsonString(listmap));
    }
    /**
     * 写入缓存
     * @param key
     * @param value
     * @param time 存在时间（秒）
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,final Redisinfo redisinfo,final Object key, List<?> listmap,final Long time) {
    	set(stringRedisTemplate, redisinfo, key, GsonUtil.toJsonString(listmap),time);
    }
    /**
     * 写入缓存
     * @param key
     * @param value
     * @param time 存在时间（秒）
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,final Redisinfo redisinfo,final Object key, final String value,final Long time) {
    	stringRedisTemplate.opsForValue().set(getkey(redisinfo, key),  value,time,TimeUnit.SECONDS);
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public static String get(final StringRedisTemplate stringRedisTemplate,Redisinfo redisinfo,final Object key) {
        return stringRedisTemplate.opsForValue().get(getkey(redisinfo,key));
        }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public static String get(final StringRedisTemplate stringRedisTemplate,String key) {
    	return stringRedisTemplate.opsForValue().get(key);
    }
    /**
     * 写入缓存
     * @param key
     * @param value
     * @param time 存在时间（秒）
     * @return
     */
    public static void set(final StringRedisTemplate stringRedisTemplate,String key, final String value) {
    	stringRedisTemplate.opsForValue().set(key,  value);
    }
    public static void set(final StringRedisTemplate stringRedisTemplate,String key, final String value,Long time) {
    	stringRedisTemplate.opsForValue().set(key,  value,time,TimeUnit.SECONDS);
    }
    /**
     * 读取缓存返回object
     * */
    @SuppressWarnings("unchecked")
	public static <T> T get(final StringRedisTemplate stringRedisTemplate,Redisinfo redisinfo,final Object key,Class<T> clazz) {
    	T t = null;
    	if(redisinfo==Redisinfo.redis_member_login)
    		t= (T) InterceptorConfig.lmap.get(key.toString());
    	if(redisinfo==Redisinfo.redis_member_user)
    		t= (T) InterceptorConfig.umap.get(key.toString());
    	if(t==null)t=GsonUtil.fromJsonString(get(stringRedisTemplate, redisinfo, key), clazz);
    	return t;
        }
    /**
     * 读取缓存返回集合
     * */
    public static  List<?> getList(final StringRedisTemplate stringRedisTemplate,Redisinfo redisinfo,final Object key,Class<?> clazz) {
    	return GsonUtil.fromJsonList(get(stringRedisTemplate, redisinfo, key), clazz);
        }
//    public static <T> List<T> getList(String s ,Class<T> clazz) {
//    	return GsonUtil.fromJsonList(s, clazz);
//    }
    
    /**
     * 存储被丢弃或者修改的数据
     */
    public static void setDtae(final RedisTemplate<String,String> mRedisTemplate,final String type,final String key, final Object value) {
    	String date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
    	mRedisTemplate.opsForHash().put(date, key + "_"+type+"_" + System.currentTimeMillis(), GsonUtil.toJsonString(value));
    }
    
    /**
     * 拿到被丢弃或者修改的数据
     */
//    public static void getDtae(final RedisTemplate<String,String> mRedisTemplate,final String key) {
//    	String date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
//    	mRedisTemplate.opsForHash().
//    }
    
    
    private static String getkey(Redisinfo re,Object key) {
//    	String a = MD5Util.MD5(key.toString());
    	String k="";
    	switch (re) {
		case redis_member_login://登录信息
			k = "login_"+key;
			break;
		case redis_member_user://用户资料信息
			k = "user_"+key;
			break;
		case getall://getall
			k = "getall_"+key;
			break;
		case exeSelectSql://exeSelectSql
			k = "exeSelectSql_"+key;
			break;
		case getbyid://getbyid
			k = "getbyid_"+key;
			break;
		case getByparameter://getByparameter
			k = "getByparameter_"+key;
			break;
		
		}
		return k;

	}

    
}
