package com.example.fw;

import com.example.fw.main.c.ZhuzhijiagouController;

import org.apache.commons.lang.time.DateFormatUtils;
import org.minbox.framework.api.boot.autoconfigure.swagger.annotation.EnableApiBootSwagger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.unit.DataSize;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
@MapperScan("com.example.*")
@ServletComponentScan//自定义拦截器
@EnableTransactionManagement//事物
@EnableApiBootSwagger
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("===============");
    }
    /**
     * 所有的打印调用这里，便于统一清除
     * */
    public static void out(Object s) {
		System.out.println(
				DateFormatUtils.format(
						new Date(), "yyyy年MM月dd日HH时mm分ss秒"
						)+ "--8001:"+s
				);
	    }
    public static void out() {
        out("");
    }
    
    /**
     * 初始化菜单
     * */
    @PostConstruct
    public void init() {
    	ZhuzhijiagouController.caidan=new ArrayList<String>();
    	ZhuzhijiagouController.caidan.add("测试菜单1");
    	ZhuzhijiagouController.caidan.add("测试菜单2");
    	ZhuzhijiagouController.caidan.add("测试菜单3");
    	ZhuzhijiagouController.caidan.add("测试菜单4");
    	ZhuzhijiagouController.anniu=new ArrayList<String>();
    	ZhuzhijiagouController.anniu.add("测试按钮1");
    	ZhuzhijiagouController.anniu.add("测试按钮2");
    	ZhuzhijiagouController.anniu.add("测试按钮3");
    	ZhuzhijiagouController.anniu.add("测试按钮4");
    	
	}
    

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(DataSize.ofBytes(104857600L));
        /// 总上传数据大小
        factory.setMaxFileSize(DataSize.ofBytes(1048576000L));
        return factory.createMultipartConfig();
    }

}
