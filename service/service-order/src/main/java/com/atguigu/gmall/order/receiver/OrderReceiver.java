package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.payment.client.PaymentFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ObjectOutput;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/13 18:52
 * @Version 1.0
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    @Resource
    private PaymentFeignClient paymentFeignClient;

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
            if (orderId!=null){
                //  根据订单Id 查询订单对象
                OrderInfo orderInfo = orderService.getOrderInfo(orderId);
                //  判断
                if(orderInfo!=null && "UNPAID".equals(orderInfo.getOrderStatus()) && "UNPAID".equals(orderInfo.getProcessStatus())){
                    //  关闭过期订单！ 还需要关闭对应的 paymentInfo ，还有alipay.
                    //  orderService.execExpiredOrder(orderId);
                    //  查询paymentInfo 是否存在！
                    PaymentInfo paymentInfo = paymentFeignClient.getPaymentInfo(orderInfo.getOutTradeNo());
                    //  判断 用户点击了扫码支付
                    if(paymentInfo!=null && "UNPAID".equals(paymentInfo.getPaymentStatus())){

                        //  查看是否有交易记录！
                        Boolean flag = paymentFeignClient.checkPayment(orderId);
                        //  判断
                        if (flag){
                            //  flag = true , 有交易记录
                            //  调用关闭接口！ 扫码未支付这样才能关闭成功！
                            Boolean result = paymentFeignClient.closePay(orderId);
                            //  判断
                            if (result){
                                //  result = true; 关闭成功！未付款！需要关闭orderInfo， paymentInfo，Alipay
                                orderService.execExpiredOrder(orderId,"2");
                            }else {
                                //  result = false; 表示付款！
                                //  说明已经付款了！ 正常付款成功都会走异步通知！
                            }
                        }else {
                            //  没有交易记录，不需要关闭支付！  需要关闭orderInfo， paymentInfo
                            orderService.execExpiredOrder(orderId,"2");
                        }

                    }else {
                        //  只关闭订单orderInfo！
                        orderService.execExpiredOrder(orderId,"1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    // 修改订单状态为已支付
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = MqConst.ROUTING_PAYMENT_PAY
    ))
    public void paySuccess(Long orderId,Message message,Channel channel){
        try {
            if(null != orderId){
                orderService.updateOrderStatusByOrderId(orderId, ProcessStatus.PAID);
                // 支付成功后，扣减库存
                orderService.subWare(orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 消费出现异常，记录到数据库中
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    // 关闭订单 退款
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_CLOSE,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE),
            key = MqConst.ROUTING_PAYMENT_CLOSE
    ))
    public void closeOrder(Long orderId,Message message,Channel channel){
        try {
            if(null != orderId){
                orderService.updateOrderStatusByOrderId(orderId,ProcessStatus.CLOSED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 消费出现异常，记录到数据库中
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_WARE_ORDER,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_WARE_ORDER),
            key = MqConst.ROUTING_WARE_ORDER
    ))
    public void initWare(String msgJson, Message message, Channel channel){
        try {
            if(!StringUtils.isEmpty(msgJson)){
                Map<String,Object> paramMap = JSON.parseObject(msgJson, Map.class);
                String orderId = (String) paramMap.get("orderId");
                String status = (String) paramMap.get("status");


                if("DEDUCTED".equals(status)){
                    // 如果扣减成功，更改订单状态
                    orderService.updateOrderStatusByOrderId(Long.parseLong(orderId),ProcessStatus.WAITING_DELEVER);
                } else {
                   // 扣减失败，修改状态，通知补货
                   orderService.updateOrderStatusByOrderId(Long.parseLong(orderId),ProcessStatus.STOCK_EXCEPTION);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // 消费失败，放入消息表
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


}
