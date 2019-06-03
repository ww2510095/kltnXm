package com.example.fw.base;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.fw.Application;
import com.example.fw.main.s.JiabiService;
import com.myjar.desutil.RunException;

@Configuration
public class MyHandlerInterceptor implements HandlerInterceptor {
	
	@Autowired
	protected JiabiService mJiabiService;
	
	/**
     * 进入controller层之前拦截请求
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	Application.out("=================测试开始=================");
    	Application.out(request.getRequestURI());
//		String phone = null;
		String k;
		String v;
		Enumeration<String> es = request.getParameterNames();
		Application.out("参数列表");
//		StringBuilder sb = new StringBuilder();
		//把参数循环迭代一遍，过滤掉异常的参数
		while (es.hasMoreElements()) {
			k = es.nextElement();
			v = request.getParameter(k);
//			sb.append(k).append("=").append(v).append(";");
			Application.out(k+"="+v+",");
			
			if ("_keyuname".equals(k))
				request.getSession().setAttribute("_keyuname", v);
			if(v instanceof String){
				/*if (v.contains("'")){
					//这个异常继承自runtimeException，不寻找c语言堆栈信息，效率比系统提供的runtimeException快了十倍
						throw new RunException("参数包含保留字(')");
					
				}*/
			}
			
		
		}
		/**
		 * 2019 04 11
		 * szy 修改验证用户是否登录
		 */
//		HttpSession mHttpSession = request.getSession();
		// 验证用户是否登录
//		if(request.getRequestURI().contains("/login")){
			// 如果是登录请求的话，就不做判断
//			User u = (User) mHttpSession.getAttribute("user");
//			int i = 0;

		/*}else*/
		
//		if (!request.getRequestURI().contains("/login")){
//			// 如果不是登录请求则需要去session中检查是否有user信息
//			User user = (User) mHttpSession.getAttribute("user");
//			if (user==null){
//				// 如果session中的user为空
//				throw new RunException("用户未登录");
//			}else {
//				// 如果session中有user并且在数据库中存在，
//				//|| mJiabiService.getUser(user.getUname())==null
//				//或者user的用户名不在数据库中，则抛出异常
//				if (mJiabiService.getUser(user.getUname())==null) {
//					// 如果登录的用户名在数据库中不存在
//					throw new RunException("用户:" + user.getUname() + "不存在");
//				}
//			}
////			int i = 0;
//		}
		
		
		
		//测试的时候把用户放到sessio里面，这样添加了参数以后就默认登录
//		if(!request.getRequestURI().contains("/user/save")){//添加新用户不做效验
//			try {
//				if(!Stringutil.isBlank(phone)){
//
//					mHttpSession.setAttribute("user", mJiabiService.getUser(phone));
//				}
//			} catch (Exception e) {
//				throw new RunException("用户:"+phone+"不存在");
//			}
//
//		}


        return true;
    }
 
 
    // --------------处理请求完成后视图渲染之前的处理操作---------------
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {
    	Application.out("处理请求完成后视图渲染之前的处理操作");
    }
 
    // ---------------视图渲染之后的操作-------------------------0
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    	Application.out("视图渲染之后的操作");
    }


}
