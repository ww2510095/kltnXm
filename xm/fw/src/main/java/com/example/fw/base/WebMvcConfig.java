package com.example.fw.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements  WebMvcConfigurer  {
	
	@Autowired
	private MyHandlerInterceptor mInterceptorConfig;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+MyParameter.Tomcat);       
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	 registry.addInterceptor(mInterceptorConfig).addPathPatterns("/**");
    }
    @Override
    public void addCorsMappings(CorsRegistry mCorsRegistry){
		mCorsRegistry.addMapping("/**")
				.allowedMethods("*")//可以用*
					.allowedOrigins("*")
				.allowedHeaders("*");
	}

	

}