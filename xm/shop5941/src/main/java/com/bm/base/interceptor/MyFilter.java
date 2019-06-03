package com.bm.base.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import com.Shop5941Application;

/**
 * 使用注解标注过滤器
 * @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器
 * 属性filterName声明过滤器的名称,可选
 * 属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性)
 * 
 */
@WebFilter(filterName="myFilter",urlPatterns="/*")
public class MyFilter implements Filter {

    public void destroy() {
		Shop5941Application.out("过滤器销毁");//当服务器关闭时调用
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {//所有的请求都会先走这个类，当第一次握手时允许跨域
    	HttpServletResponse resp = (HttpServletResponse)response;
//    	HttpServletRequest req = (HttpServletRequest)request;
//    	String ss =req.getMethod();
//    	Application.out("-----------------------跨域处理-------------------------------");
//    	Application.out(ss);
    	//"*"存在风险，建议指定可信任的域名来接收响应信息，如"http://www.sosoapi.com"
    	resp.addHeader("Access-Control-Allow-Origin", "*");
    	//如果存在自定义的header参数，需要在此处添加，逗号分隔
    	resp.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, "
    			+ "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, "
    			+ "Content-Type, X-E4M-With");
    	resp.addHeader("Access-Control-Allow-Methods", "GET, POST");  
    	resp.addHeader("Access-Control-Allow-Credentials", "true");
    	chain.doFilter(request, response);
  
    }

    public void init(FilterConfig config) throws ServletException {
		Shop5941Application.out("过滤器初始化");//当服务器启动时调用
    }

}