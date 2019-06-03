package com.bm.base.interceptor;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.bm.base.MyParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private InterceptorConfig mInterceptorConfig;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+MyParameter.Tomcat);       
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截规则：
        registry.addInterceptor(mInterceptorConfig).addPathPatterns("/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //新建一个解析试图
        FastJsonHttpMessageConverter mFastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig FastJsonConfig = new FastJsonConfig();
        FastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullStringAsEmpty

        );

        mFastJsonHttpMessageConverter.setFastJsonConfig(FastJsonConfig);
        //将试图添加到列表内
        converters.add(mFastJsonHttpMessageConverter);

    }





}