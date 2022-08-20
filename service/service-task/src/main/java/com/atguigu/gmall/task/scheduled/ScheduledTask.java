package com.atguigu.gmall.task.scheduled;

import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.service.constant.MqConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author dushuo
 * @Date 2022/8/17 20:34
 * @Version 1.0
 */
@Component
@EnableScheduling
@Slf4j
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void task(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,  MqConst.ROUTING_TASK_1, "");
    }

}
