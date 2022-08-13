package com.atguigu.gmall.common.service.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.service.model.GmallCorrelationData;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @Author dushuo
 * @Date 2022/8/12 18:45
 * @Version 1.0
 */
@Component
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 消息发送到了交换机
     * @param correlationData 消息
     * @param ack 交换机收到了
     * @param cause 原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(ack){
            System.out.println("消息发送到了交换机"+JSON.toJSONString(correlationData));
        }else {
            System.out.println("消息没有发送到交换机,原因是:"+cause+"数据:"+ JSON.toJSONString(correlationData));
            this.retrySendMsg(correlationData);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 反序列化对象输出
        System.out.println("消息主体: " + new String(message.getBody()));
        System.out.println("应答码: " + replyCode);
        System.out.println("描述：" + replyText);
        System.out.println("消息使用的交换器 exchange : " + exchange);
        System.out.println("消息使用的路由键 routing : " + routingKey);
        String correlationId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        String strJson = (String) redisTemplate.opsForValue().get(correlationId);
        GmallCorrelationData gmallCorrelationData = JSONObject.parseObject(strJson, GmallCorrelationData.class);
        this.retrySendMsg(gmallCorrelationData);
    }

    private void retrySendMsg(CorrelationData correlationData) {
        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
        int retryCount = gmallCorrelationData.getRetryCount();
        if(retryCount > 3){
            System.out.println("重试次数已用完");
            return;
        }else {
            System.out.println("第"+retryCount+"次尝试");
            retryCount++;
            gmallCorrelationData.setRetryCount(retryCount);
            redisTemplate.opsForValue().set(gmallCorrelationData.getId(), JSONObject.toJSONString(gmallCorrelationData),10, TimeUnit.MINUTES);
            boolean result = gmallCorrelationData.isDelay();
            if(result){
                // 如果是延迟消息，则发送延迟队列
                rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(),gmallCorrelationData.getRoutingKey(),gmallCorrelationData.getMessage(),message -> {
                    message.getMessageProperties().setDelay(gmallCorrelationData.getDelayTime());
                    return message;
                },gmallCorrelationData);
            } else {
                rabbitTemplate.convertAndSend(
                        gmallCorrelationData.getExchange(),
                        gmallCorrelationData.getRoutingKey(),
                        gmallCorrelationData.getMessage(),gmallCorrelationData);
            }

        }
    }
}
