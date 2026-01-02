//package com.akyabhishek.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController("/health")
//public class RedisHealthController {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @GetMapping("/redis")
//    public String checkRedis() {
//        try {
//            redisTemplate.getConnectionFactory().getConnection().ping();
//            return "Redis is UP";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Redis is DOWN: " + e.getMessage();
//        }
//    }
//}
