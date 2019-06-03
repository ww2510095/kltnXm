package com.bm.base.interceptor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.Shop5941Application;

/**
 * 使用@WebListener注解，实现ServletContextListener接口
 *
 */
@WebListener
public class MyServletContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        Shop5941Application.out("====================================系统初始化===================================");
        //先预留起来，说不定有别的用途
        
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}