package com.bm.base.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.Shop5941Application;
import com.bm.LogData;
import com.bm.auths.MemberAuths;
import com.bm.base.BaseDao;
import com.bm.base.MyParameter;
import com.bm.base.MyParameter.Redisinfo;
import com.bm.base.interceptor.Auth_wx.x_key_ke;
import com.bm.base.redis.RedisMemberType;
import com.bm.base.redis.RedisUtils;
import com.bm.wx.xuankuan.u.X_u;
import com.myjar.Stringutil;
import com.myjar.desutil.RunException;

@Configuration
public class InterceptorConfig implements HandlerInterceptor {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private BaseDao mBaseDao;

//	public static volatile long time;
//	private static volatile String name = "";
	
	public static UMap umap = null;
	public static Lmap lmap = null;;

	/**
	 * 进入controller层之前拦截请求
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setAttribute("runTime",System.currentTimeMillis());
		if(!MyParameter.SERVICE_TURE){
			throw new RunException("抱歉，服务器繁忙，请隔十秒以后再来");
		}
		if(umap==null)umap=new UMap(mBaseDao);
		if(lmap==null)lmap=new Lmap(mBaseDao, stringRedisTemplate);

		Shop5941Application.out(
				"=======================================测试开始==================================================");
//		setSystime();
		String phone = null;
		String uid = null;
		String k;
		String v;
		Enumeration<String> es = request.getParameterNames();
		Shop5941Application.out("参数列表");
		StringBuilder sb = new StringBuilder();
		while (es.hasMoreElements()) {
			k = es.nextElement();
			v = request.getParameter(k);
			
			sb.append(k).append("=").append(v).append(";");

			Shop5941Application.out(k+"="+v+",");
			
			if ("uname".equals(k))
				phone = v;
			else if ("UID".equals(k))
				uid = v;
			if (v.contains("'")){
				if(!request.getRequestURI().contains("erp_number_add"))//经过赛选的密文不错过滤
					throw new RunException("参数包含保留字(')");
				
			}
				
			
//			if("null".equals(v))
//				request.
			
			
		}
		//添加一个日志
		if(!request.getRequestURI().contains("error"))
			LogData.start(mBaseDao,Stringutil.isBlank(phone)?"游客":phone, getIpAddress(request), request.getRequestURI(), sb.toString());
		HandlerMethod method = null;
		try {
			method = (HandlerMethod) handler;
//			setName(request.getRequestURI());
			if (!MyParameter.auths_true)
				return true; // 权限模块未开启
		} catch (Exception e) {
			return true;
		}
		Auth_wx mAuth_wx = method.getMethodAnnotation(Auth_wx.class);
		if(mAuth_wx!=null){
			if("123".equals(uid))return true;
			x_key_ke[] mx_key_ke=mAuth_wx.x_key();
			if(mx_key_ke.length>0){
				if(Stringutil.isBlank(uid))throw new RunException("令牌错误");
				X_u mX_u= RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_user, uid,X_u.class);
				if(mX_u==null)throw new RunException("登录超时");
				mX_u.setX_z_time(System.currentTimeMillis());
				RedisUtils.set(stringRedisTemplate, Redisinfo.redis_member_user, uid, mX_u);
				if(mX_u.getEx()!=x_key_ke.admin.getKey()){
					boolean b = false;
					for (x_key_ke x_key_ke_s : mx_key_ke) {
						if(x_key_ke_s.getKey()==mX_u.getEx()){
							b=true;
							break;
						}
					}
					if(!b)throw new RunException("权限不足");
				}
				
				
			}
			
		}
		
		Auth auth = method.getMethodAnnotation(Auth.class);
		if (auth != null) {
			if (Stringutil.isBlank(phone))
				throw new RunException("账号不可为空");
			if (Stringutil.isBlank(uid))
				throw new RunException("设备号不可为空");
			/** 登录验证 */
			uid = ridesMember(phone, uid) + "";
			// 权限
			MemberAuths mMemberAuths = RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_user, uid,
					MemberAuths.class);
			if (mMemberAuths.getSuperadmin() == 1)
				return true;// 超级管理员默认拥有所有权限
			// 验证管理员权限
			if (auth.admin())
				throw new RunException("权限不足");
			
			//其他权限
			RedisMemberType mRedisMemberType = RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_login, phone,
					RedisMemberType.class);
			if(mRedisMemberType==null)
				throw new RunException("登陆超时");
			
//			List<String> ls = mMemberAuths.getAuths();
//			
//			if(mRedisMemberType.getType()==1){
//				// @formatter:off
//				 for (String string : ls) {
//					 String stc =request.getRequestURI();
//					 stc=stc.replace("/", "\\");
//					 stc=stc.replace("\\\\", "\\");
//					 
//					 string=string.replace("/", "\\");
//					 string=string.replace("\\\\", "\\");
//					
//					if(stc.equals(string))
//						return true;
//				 	}
//				// @formatter:on
//				 throw new AuthException("权限不足");
//				
//			}
//			
//			RedisMemberType mRedisMemberType = RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_login, phone,
//					RedisMemberType.class);
//			if(mRedisMemberType==null)
//				throw new RunException("登陆超时");
//			
//			List<String> ls = mMemberAuths.getAuths();
//			
//			if(mRedisMemberType.getType()==1){
//				// @formatter:off
//				 for (String string : ls) {
//					if(request.getRequestURI().equals(string))
//						return true;
//				 	}
//				// @formatter:on
//				 throw new AuthException("权限不足");
//				
//			}else{
//				// 店铺权限
//				Shop(ls, auth.Shop());
//				// 导入店铺与审核权限
//				Shopback(ls, auth.Shopback());
//				// 商品权限
//				Commodity(ls, auth.Commodity());
//				// 店铺账单权限
//				consumptionShop(ls, auth.consumptionShop());
//				// 是否可以导入店铺excel
////				if (auth.shopExcel())
////					shopExcel(ls);
//				// 是否可以导出报表
//				if (auth.export())
//					export(ls);
//				// 是否可以查询销售阈值
//				if (auth.Orderrelevance())
//					Orderrelevance(ls);
//				// 是否可以操作订单相关资料
//				if (auth.orders())
//					orders(ls);
//				// 是否可以发布消息
//				if (auth.SystemMessage())
//					SystemMessage(ls);
//				// 是否可以查询结算规则
//				if (auth.ORDERSRULE())
//					ORDERSRULE(ls);
//				// 是否可以查询赔付
//				if (auth.Express())
//					Express(ls);
//				// 是否可以添加店员
//				if (auth.clerk())
//					clerk(ls);
//				// 活动相关
//				Promotion(ls, auth.Promotion());
//				// 库存相关
//				stock(ls, auth.Promotion());
//				// 退货操作
//				Returngoods(ls, auth.Returngoods());
//				// 导入商品
//				commodityback(ls, auth.commodityback());
//				// 优惠券
//				coupon(ls, auth.coupon());
//			}

			
			
		}

		return true;
	}

//	private void commodityback(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.COMMODITYBACK_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.COMMODITYBACK_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.COMMODITYBACK_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.COMMODITYBACK_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//	private void coupon(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.COUPON_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.COUPON_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.COUPON_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.COUPON_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	private void Returngoods(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.RETURNGOODS_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.RETURNGOODS_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.RETURNGOODS_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.RETURNGOODS_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	private void Promotion(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.PROMOTION_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.PROMOTION_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.PROMOTION_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.PROMOTION_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//	private void stock(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.STOCK_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.STOCK_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.STOCK_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.STOCK_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	private void SystemMessage(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.SYSTEMMESSAGE))
//				return;
//		throw new AuthException("权限不足");
//	}
//	private void Express(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.EXPRESS))
//				return;
//		throw new AuthException("权限不足");
//	}
//	private void clerk(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.CLERK))
//				return;
//		throw new AuthException("权限不足");
//	}
//	private void ORDERSRULE(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.ORDERSRULE))
//				return;
//		throw new AuthException("权限不足");
//	}
//
//	private void Orderrelevance(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.ORDERRELEVANCE))
//				return;
//		throw new AuthException("权限不足");
//	}
//	private void orders(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.ORDERS))
//				return;
//		throw new AuthException("权限不足");
//	}
//
//	private void export(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.EXPORT))
//				return;
//		throw new AuthException("权限不足");
//	}

	// --------------处理请求完成后视图渲染之前的处理操作---------------
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
	}

	// ---------------视图渲染之后的操作-------------------------0
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
	//	Long runTime =Long.valueOf(httpServletRequest.getAttribute("runTime").toString());
	//	System.out.println("运行时间:"+(System.currentTimeMillis()-runTime));
//		long t = getSystime();
//		if (!getName().contains("/error"))
//			Application.out("测试方法：" + getName() + "运行完毕，总共耗时：" + (System.currentTimeMillis() - t) + "毫秒");
//		
//		Application.out(new Date());

	}

//	private synchronized void setSystime() throws InterruptedException {
//		time = System.currentTimeMillis();
//	}
//
//	private synchronized long getSystime() throws InterruptedException {
//		return time;
//	}
//
//	private synchronized String getName() throws InterruptedException {
//		return name;
//	}
//
//	private synchronized void setName(String name1) throws InterruptedException {
//		name = name1;
//	}

	/**
	 * 采用rides技术，验证用户是否登录
	 */
	private Long ridesMember(String phone, String uid) throws Exception {
		return lmap.get(phone).getUserid();
//		RedisMemberType rmt = RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_login, phone,
//				RedisMemberType.class);
//		if (rmt == null)
//			throw new AuthException("登录超时");
//		MemberAuths mMemberAuths= RedisUtils.get(stringRedisTemplate, Redisinfo.redis_member_user, rmt.getUserid(),MemberAuths.class);
//    	if(mMemberAuths==null) throw new RunException("登录超时");
//    	
//    	if(mMemberAuths.getSuperadmin()==1) return rmt.getUserid();
//    	
////    	if(mMemberAuths.getmShop()!=null)
//    		
//		
//		if(rmt.getType()==1){
////	    	if(mMemberAuths.getmShop()==null)
////	    		throw new AuthException("权限不足");
////			if (!uid.equals(rmt.getWEBID()))
////				throw new AuthException("登录超时");
//		}else{
//			if (!rmt.getUID().equals(uid))
//				throw new AuthException("登录超时");
//		}
//		return rmt.getUserid();
	}

//	/**
//	 * 店铺权限
//	 */
//	private void Shop(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.SHOP_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.SHOP_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.SHOP_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.SHOP_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	/**
//	 * //导入店铺与审核权限
//	 */
//	private void Shopback(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.SHOPBACK_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.SHOPBACK_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.SHOPBACK_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.SHOPBACK_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	/**
//	 * // 商品模型权限
//	 */
//	private void Commodity(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.COMMODITY_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.COMMODITY_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.COMMODITY_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.COMMODITY_DELETE;
//		}
//		authsAll(ls, s1);
//	}
//
//	/**
//	 * //店铺账单权限
//	 */
//	private void consumptionShop(List<String> ls, Administration[] as) {
//		int size = as.length;
//		String[] s1 = new String[size];
//		for (int i = 0; i < size; i++) {
//			if (as[i] == Administration.ADD)
//				s1[i] = AuthsAll.CONSUMPTIONSHOP_ADD;
//			else if (as[i] == Administration.UPDATE)
//				s1[i] = AuthsAll.CONSUMPTIONSHOP_UPDATE;
//			else if (as[i] == Administration.SELECT)
//				s1[i] = AuthsAll.CONSUMPTIONSHOP_SELECT;
//			else if (as[i] == Administration.DELETE)
//				s1[i] = AuthsAll.CONSUMPTIONSHOP_DELETE;
//		}
//		authsAll(ls, s1);
//	}

//	/**
//	 * 是否可以导入店铺excel
//	 */
//	private void shopExcel(List<String> ls) {
//		for (String string : ls)
//			if (string.equals(AuthsAll.SHOPEXCEL))
//				return;
//
//		throw new AuthException("权限不足");
//	}

	/**
	 * //验证管理员权限
	 */

//	private void admin(Member ma) {
//		if (ma.getSuperadmin() != 1)
//			throw new AuthException("权限不足");
//	}
//
//	private void authsAll(List<String> ls, String[] as) {
//		if (as.length == 0)
//			return;
//		int i = as.length;
//		boolean b = false;
//		for (String string : ls) {
//			b = false;
//			for (String mstring : as) {
//				if (string.equals(mstring)) {
//					b = true;
//					i--;
//					break;
//				}
//			}
//			if (!b)
//				new AuthException("权限不足");
//		}
//
//		if (i != 0)
//			throw new AuthException("权限不足");
//
//	}
	 public static String getIpAddress(HttpServletRequest request) {  
	        String ip = request.getHeader("x-forwarded-for");  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("WL-Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_CLIENT_IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getRemoteAddr();  
	        }  
	        return ip;  
	    }  
}
