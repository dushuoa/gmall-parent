package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.mq.config.DelayedMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author dushuo
 * @Date 2022/8/12 18:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/mq")
public class MqController {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendConfirm")
    public Result sendConfirm(){
        rabbitService.sendMessage("exchange.confirm","routingKey.confirm","你好，我叫赛利亚");
        return Result.ok();
    }

    @GetMapping("sendDelay")
    public Result sendDelay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = format.format(new Date());
        System.out.println("发送时间:"+currentDate);
        rabbitService.sendDelay(DelayedMqConfig.exchange_delay,DelayedMqConfig.routing_delay,"你好",3*1000);
//        rabbitTemplate.convertAndSend(DelayedMqConfig.exchange_delay,DelayedMqConfig.routing_delay,"你好",message -> {
//            message.getMessageProperties().setDelay(3*1000);
//            return message;
//        });
        return Result.ok();
    }




}
