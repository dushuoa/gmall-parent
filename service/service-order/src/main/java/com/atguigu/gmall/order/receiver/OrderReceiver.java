package com.atguigu.gmall.order.receiver;

import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author dushuo
 * @Date 2022/8/13 18:52
 * @Version 1.0
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    // 取消订单 本质就是修改状态
    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void cancelOrder(Long orderId, Message message, Channel channel){
        /**
         * 防止消息重复消费
         *      1. 利用数据库去重表
         *      2. 利用redis的setNX
         *      3. redis的incr
         *      4. 数据库的业务字段 √
         */
        try {
            if(orderId!=null){
                OrderInfo orderInfo = orderService.getOrderInfo(orderId);

                if(orderInfo.getOrderStatus().equals("UNPAID") && orderInfo.getProcessStatus().equals("UNPAID")){
                    orderService.updateOrderStatus(orderId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }

}
