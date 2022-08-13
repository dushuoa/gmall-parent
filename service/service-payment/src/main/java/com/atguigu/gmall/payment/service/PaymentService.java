package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @Author dushuo
 * @Date 2022/8/13 19:44
 * @Version 1.0
 */
public interface PaymentService {

    /**
     * 保存交易记录
     * @param orderInfo 订单信息
     * @param paymentType 支付类型
     */
    void savePaymentInfo(OrderInfo orderInfo,String paymentType);
}
