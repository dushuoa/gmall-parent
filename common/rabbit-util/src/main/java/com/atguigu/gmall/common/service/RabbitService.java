package com.atguigu.gmall.common.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.service.model.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author dushuo
 * @Date 2022/8/12 18:24
 * @Version 1.0
 */
@Component
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public Boolean sendMessage(String exchange,String routingKey,Object msg){
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        gmallCorrelationData.setMessage(msg);
        gmallCorrelationData.setExchange(exchange);
        gmallCorrelationData.setRoutingKey(routingKey);
        String id = UUID.randomUUID().toString().replaceAll("-","");
        gmallCorrelationData.setId(id);
        // 发送消息前，放到redis中，方便后面重试机制
        redisTemplate.opsForValue().set(id, JSONObject.toJSONString(gmallCorrelationData),10, TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(exchange,routingKey,msg,gmallCorrelationData);
        return true;
    }



}
