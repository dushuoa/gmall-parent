package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

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

    /**
     * 根据outTradeNo 和 支付方式 查询数据！
     * @param outTradeNo 订单编号
     * @param paymentType 支付方式
     * @return 订单数据
     */
    PaymentInfo getPaymentInfo(String outTradeNo, String paymentType);

    /**
     * 修改交易记录状态！再订单状态！
     * @param outTradeNo 订单编号
     * @param paymentType 支付方式
     * @param paramsMap 参数
     */
    void paySuccess(String outTradeNo, String paymentType, Map<String, String> paramsMap);

    /**
     * 修改支付记录状态
     * @param outTradeNo 订单编号
     * @param paymentType 支付方式
     * @param paymentInfo 要修改的内容
     */
    void updatePaymentInfo(String outTradeNo, String paymentType, PaymentInfo paymentInfo);


}
