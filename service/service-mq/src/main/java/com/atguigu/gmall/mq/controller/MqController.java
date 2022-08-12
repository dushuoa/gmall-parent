package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("sendConfirm")
    public Result sendConfirm(){
        rabbitService.sendMessage("exchange.confirm","routingKey.confirm","你好，我叫赛利亚");
        return Result.ok();
    }



}
