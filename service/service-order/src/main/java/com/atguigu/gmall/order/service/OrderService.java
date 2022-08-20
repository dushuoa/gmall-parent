package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/10 19:06
 * @Version 1.0
 */
public interface OrderService {

    /**
     * 保存订单数据
     * @param orderInfo 前端传过来的数据
     * @return 订单id
     */
    Long submitOrder(OrderInfo orderInfo);

    /**
     * 设置一个tradeNo，防止用户回退未刷新重新提交 当用户点击到订单页面的时候就要重新生成
     * @param userId 用户id
     * @return tradeNo
     */
    String getTradeNo(String userId);

    /**
     * 判断tradeNo和Redis中的tradeNo是否相等
     * @param userId 用户id
     * @param tradeNo 流水号
     * @return true
     */
    Boolean isTradeNoEqualsRedis(String userId,String tradeNo);

    /**
     * 删除缓存
     * @param userId 用户id
     */
    void deleteRedis(String userId);

    /**
     * 验证库存
     * @param skuId 用户id
     * @param skuNum skuNum
     * @return true or false
     */
    boolean checkStock(Long skuId, Integer skuNum);

    /**
     * 我的订单
     * @param pageParam 封装分页参数
     * @param userId 用户id
     * @return 结果
     */
    IPage<OrderInfo> getMyOrderPage(Page<OrderInfo> pageParam, String userId);

    /**
     * 根据订单id获取订单的详细数据
     * @param orderId 订单id
     * @return 订单的详细数据
     */
    OrderInfo getOrderInfo(Long orderId);

    /**
     * 根据订单id修改订单状态
     * @param orderId 订单id
     */
    void updateOrderStatus(Long orderId);

    /**
     * 根据订单id修改订单状态为成功
     * @param orderId 订单id
     * @param processStatus 流程
     */
    public void updateOrderStatusByOrderId(Long orderId, ProcessStatus processStatus);

    /**
     * 支付成功后，扣减库存
     * @param orderId 订单id
     */
    void subWare(Long orderId);

    /**
     * 拆分订单
     * @param orderId 订单Id
     * @param wareSkuMap 请求参数
     * @return 拆分后订单以及子订单
     */
    List<OrderInfo> orderSplit(long orderId, String wareSkuMap);

    /**
     * 根据对象转换为map
     * @param orderInfo 订单对象
     * @return map
     */
    Map<String, Object> getMapByOrderInfo(OrderInfo orderInfo);

    /**
     * 更新过期订单
     * @param orderId 订单Id
     * @param flag 是否删除成功
     */
    void execExpiredOrder(Long orderId,String flag);

}
