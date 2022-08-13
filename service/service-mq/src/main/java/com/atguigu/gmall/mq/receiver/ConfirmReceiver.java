package com.atguigu.gmall.mq.receiver;

import com.atguigu.gmall.mq.config.DelayedMqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author dushuo
 * @Date 2022/8/12 18:33
 * @Version 1.0
 */
@Component
public class ConfirmReceiver {


    @RabbitListener(bindings = {
        @QueueBinding(
                value = @Queue(value = "queue.confirm",durable = "true",autoDelete = "false"),
                exchange = @Exchange(value = "exchange.confirm",durable = "true",autoDelete = "false"),
                key = "routingKey.confirm")
    })
    public void process(Message message, Channel channel) throws IOException {
        System.out.println(new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(queues = DelayedMqConfig.queue_delay_1)
    public void get(String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("接收时间: " + sdf.format(new Date()) + " Delay rece." + msg);
    }
}
