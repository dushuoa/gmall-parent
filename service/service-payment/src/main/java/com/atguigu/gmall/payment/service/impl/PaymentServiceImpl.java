package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/13 19:45
 * @Version 1.0
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AlipayClient alipayClient;

    @Resource
    private OrderFeignClient orderFeignClient;

    @Resource
    private RabbitService rabbitService;

    // 保存订单信息
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderInfo.getId());
        wrapper.eq("payment_type",paymentType);
        PaymentInfo info = paymentInfoMapper.selectOne(wrapper);
        if(info == null){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCreateTime(new Date());
            paymentInfo.setOrderId(orderInfo.getId());
            paymentInfo.setPaymentType(paymentType);
            paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
            paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
            paymentInfo.setSubject(orderInfo.getTradeBody());
            //paymentInfo.setSubject("test");
            paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
            paymentInfoMapper.insert(paymentInfo);
        }
    }

    // 根据outTradeNo 和 支付方式 查询数据！
    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",outTradeNo);
        wrapper.eq("payment_type",paymentType);
        return paymentInfoMapper.selectOne(wrapper);
    }

    // 修改交易记录状态！再订单状态！ 支付
    @Override
    public void paySuccess(String outTradeNo, String paymentType, Map<String, String> paramsMap) {

        // 根据订单编号查询orderId
        PaymentInfo info = this.getPaymentInfo(outTradeNo, paymentType);

        try {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo.setUpdateTime(new Date());
            paymentInfo.setCallbackContent(paramsMap.toString());
            paymentInfo.setTradeNo(paramsMap.get("trade_no"));

            this.updatePaymentInfo(outTradeNo,paymentType,paymentInfo);
            // TODO 发送MQ修改订单状态
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,MqConst.ROUTING_PAYMENT_PAY,info.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，删除缓存，允许重试
            redisTemplate.delete(paramsMap.get("notify_id"));
        }
    }

    // 修改支付记录状态
    @Override
    public void updatePaymentInfo(String outTradeNo, String paymentType, PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",outTradeNo);
        wrapper.eq("payment_type",paymentType);
        paymentInfoMapper.update(paymentInfo,wrapper);
    }




}
