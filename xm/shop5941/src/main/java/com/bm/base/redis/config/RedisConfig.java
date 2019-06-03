//package com.bm.base.redis.config;
//
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@Configuration
//@EnableCaching
//public class RedisConfig extends CachingConfigurerSupport{
//
//    @Bean
//    public CacheManager cacheManager(RedisTemplate<?,?> redisTemplate) {
//        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
//        //设置缓存过期时间(秒)
//        rcm.setDefaultExpiration(600);
//        return rcm;
//    }
//
//
//}
