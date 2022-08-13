package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author dushuo
 * @Date 2022/8/13 19:45
 * @Version 1.0
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    // 保存订单信息
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderInfo.getId());
        wrapper.eq("payment_type",paymentType);
        PaymentInfo info = paymentInfoMapper.selectOne(wrapper);
        if(info != null){
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
}
