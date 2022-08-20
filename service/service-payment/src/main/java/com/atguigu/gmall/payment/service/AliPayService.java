package com.atguigu.gmall.payment.service;

/**
 * @Author dushuo
 * @Date 2022/8/14 12:12
 * @Version 1.0
 */
public interface AliPayService {

    /**
     * 创建二维码
     * @param orderId 订单Id
     * @return 二维码的表单
     */
    String createaliPay(Long orderId);

    /**
     * 退款接口
     * @param orderId 订单Id
     * @return true false
     */
    boolean refund(Long orderId);

    /***
     * 关闭交易
     * @param orderId 订单Id
     * @return true false
     */
    Boolean closePay(Long orderId);


    /**
     * 根据订单查询是否支付成功！
     * @param orderId 订单Id
     * @return true false
     */
    Boolean checkPayment(Long orderId);


}
