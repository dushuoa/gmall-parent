package com.atguigu.gmall.list.receiver;

import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.list.service.SearchService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author dushuo
 * @Date 2022/8/13 16:16
 * @Version 1.0
 */
@Component
public class ListReceiver {

    @Autowired
    private SearchService searchService;


    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.ROUTING_GOODS_UPPER,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            key = MqConst.ROUTING_GOODS_UPPER
    ))
    public void upperGoods(Long skuId, Message message, Channel channel){
        // 商品上架
        try {
            if(skuId != null){
                searchService.upperGoods(skuId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.ROUTING_GOODS_LOWER,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            key = MqConst.ROUTING_GOODS_LOWER
    ))
    public void lowerGoods(Long skuId, Message message, Channel channel){
        // 商品下架
        try {
            if(skuId != null){
                searchService.lowerGoods(skuId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
