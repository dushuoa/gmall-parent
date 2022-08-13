package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/13 16:44
 * @Version 1.0
 * 基于插件的方式实现延迟队列
 */
@Configuration
public class DelayedMqConfig {

    public static final String exchange_delay = "exchange.delay";
    public static final String routing_delay = "routing.delay";
    public static final String queue_delay_1 = "queue.delay.1";

    @Bean
    public Exchange exchange(){
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(exchange_delay,"x-delayed-message",true,false,args);
    }

    @Bean
    public Queue queue(){
        return new Queue(queue_delay_1,true,false,false);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(exchange()).with(routing_delay).noargs();
    }



}
