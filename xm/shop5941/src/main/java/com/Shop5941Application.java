package com;

import java.io.File;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.catalina.connector.Connector;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.minbox.framework.api.boot.autoconfigure.swagger.annotation.EnableApiBootSwagger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableApiBootSwagger
@SpringBootApplication//启动
@MapperScan("com.bm.*")//扫描
@EnableScheduling//任务计划
@ServletComponentScan//自定义拦截器
@EnableTransactionManagement //事务
//@Transactional   // 需要执行事务的方法需要添加改注解
public class Shop5941Application {
   /**
    * 启动
    * */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Shop5941Application.class, args);
        System.out.println("5941微服启动完毕");
    }
    /**
     * 所有的打印调用这里，便于统一清除
     * */
    public static void out(Object s) {
		//System.out.println(DateFormatUtils.format(new Date(), "yyyy年MM月dd日HH时mm分ss秒")+ "--8091:"+s);
    }
    public static void out() {
    	out("");
    }
  
   
    
    /**
     * 项目初始化
     * */
    @PostConstruct
    public void init() {
    	initPackage();//初始化组件
    	gc();//回收垃圾
	}
    
	private void initPackage() {
		new com.bm.user.A();
		out("用户模块加载成功");
		new com.bm.consumption.A();
		out("钱包模块加载成功");
		new com.bm.shop.A();
		out("店铺模块加载成功");
		new com.bm.commodity.A();
		out("商品模块加载成功");
		new com.bm.file.A();
		out("文件模块加载成功");
		new com.bm.auths.A();
		out("权限模块加载成功");
		new com.bm.orders.A();
		out("订单模块加载成功");
		new com.bm.friends.A();
		out("好友模块加载成功");
		new com.bm.clerk.A();
		out("店员模块加载成功");
		new com.bm.stock.A();
		out("库存模块加载成功");
		new com.bm.ordersRule.A();
		out("分销规则加载成功");
		new com.bm.myaddress.A();
		out("收货地址加载成功");
		new com.bm.evaluate.A();
		out("评价功能加载成功");
		new com.bm.shoppingcard.A();
		out("购物车加载成功");
		new com.bm.promotion.A();
		out("促销规则加载成功");
		new com.bm.returngoods.A();
		out("退货规则加载成功");
		new com.bm.collection.A();
		out("收藏模块加载成功");
		new com.bm.search.A();
		out("热门搜索加载成功");
		new com.bm.feedback.A();
		out("一键反馈加载成功");
		new com.bm.systemMessage.A();
		out("消息加载成功");
		new com.bm.express.A();
		out("运费险加载成功");
		new com.bm.freeshipping.A();
		out("包邮规则加载成功");
		new com.bm.coupon.A();
		out("优惠券加载成功");
		new com.bm.version.A();
		out("版本更新加载成功");
		new com.bm.putforward.A();
		out("提现功能加载成功");
		new com.bm.clerk.identity.A();
		out("导购角色加载成功");
		new com.bm.clerk.commission.A();
		out("结算相关加载成功");
		new com.bm.help.A();
		out("帮助中心价载成功");
		new com.bm.orders.ms.A();
		out("秒杀价载成功");
		new com.bm.orders.ys.A();
		out("预售加载成功");
		new com.bm.orders.pdd.A();
		out("拼团加载成功");
		new com.bm.ordersRule.gd.A();
		out("固定分成加载成功");
		new com.bm.consumption.envelopes.A();
		out("红包加载成功");
		new com.bm.user.goldcoin.A();
		out("积分加载成功");
		new com.bm.user.goldcoincoupon.A();
		out("积分换优惠价加载成功");
		
		new com.bm.user.goldcoincoupon.A();
		out("积分换优惠价加载成功");
		new com.bm.orders.gfakchd.A();
		out("高仿爱库存模块");
		
		
		
		new com.bm.orders.cj.A();
		out("抽奖加载成功");
		new com.bm.orders.orderscard.A();
		out("实物卡加载成功");
	}
	
	
	private void gc() {
		System.gc();
	}

	 /**
     * 初始化Redis
     * */
    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
    	return new StringRedisTemplate(connectionFactory);
    }
	
	@Value("${https.port}")
    private Integer port;

    @Value("${https.ssl.key-store-password}")
    private String key_store_password;

    @Value("${https.ssl.key-password}")
    private String key_password;
    
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.addAdditionalTomcatConnectors(createSslConnector()); // 添加http
//        return tomcat;
//    }
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }
 // 配置https
    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        File keystore = new File("C:\\config\\www.bming.net.jks");
		/*File truststore = new ClassPathResource("sample.jks").getFile();*/
		connector.setScheme("https");
		connector.setSecure(true);
		connector.setPort(port);
		protocol.setSSLEnabled(true);
		protocol.setKeystoreFile(keystore.getAbsolutePath());
		protocol.setKeystorePass(key_store_password);
		protocol.setKeyPass(key_password);
		return connector;
    }

    
}