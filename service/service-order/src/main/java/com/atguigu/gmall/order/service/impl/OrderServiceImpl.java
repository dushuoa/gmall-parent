package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.mapper.OrderServiceMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author dushuo
 * @Date 2022/8/10 19:08
 * @Version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderServiceMapper orderServiceMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private RabbitService rabbitService;

    private String wareUrl = "http://localhost:9001";

    // 保存订单数据
    @Override
    public Long submitOrder(OrderInfo orderInfo) {
        // 插入购物车数据
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        String outTradeNo = "ATGUIGU"+System.currentTimeMillis()+ new Random().nextInt(10000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTradeBody("红浪漫68号技师");
        orderInfo.setOperateTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderServiceMapper.insert(orderInfo);

        // 添加订单详细数据
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        });
        rabbitService.sendDelay(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL,orderInfo.getId(),3*1000);
        return orderInfo.getId();
    }

    // 设置一个tradeNo
    @Override
    public String getTradeNo(String userId) {
        String key = "trade:"+userId;
        String tradeNo = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(key,tradeNo);
        return tradeNo;
    }

    // 判断用户传过来的和redis中是否相等
    @Override
    public Boolean isTradeNoEqualsRedis(String userId, String tradeNo) {
        String key = "trade:"+userId;
        String tradeRedis = (String) redisTemplate.opsForValue().get(key);
        return tradeNo.equals(tradeRedis);
    }

    // 删除缓存
    @Override
    public void deleteRedis(String userId) {
        String key = "trade:"+userId;
        redisTemplate.delete(key);
    }

    // 验证库存
    @Override
    public boolean checkStock(Long skuId, Integer skuNum) {
        // 远程调用http://localhost:9001/hasStock?skuId=10221&num=2
        String uri = wareUrl+"/hasStock?skuId="+skuId+"&num="+skuNum;
        String result = HttpClientUtil.doGet(uri);

        return "1".equals(result);
    }

    // 我的订单
    @Override
    public IPage<OrderInfo> getMyOrderPage(Page<OrderInfo> pageParam, String userId) {
        IPage<OrderInfo> pageResult = orderInfoMapper.selectMyOrderPage(pageParam, userId);
        List<OrderInfo> orderInfoList = pageResult.getRecords();
        orderInfoList.forEach(orderInfo -> {
            orderInfo.setOrderStatusName(OrderStatus.getStatusNameByStatus(orderInfo.getOrderStatus()));
        });
        return pageResult;
    }

    // 根据订单id获取订单的详细数据
    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(orderInfo!=null){
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orderId));
            orderInfo.setOrderDetailList(orderDetailList);
        }
        return orderInfo;
    }

    // 根据订单id修改订单状态
    @Override
    public void updateOrderStatus(Long orderId) {
        this.updateOrderStatusByOrderId(orderId,ProcessStatus.CLOSED);
    }

    private void updateOrderStatusByOrderId(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setProcessStatus(processStatus.name());
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setUpdateTime(new Date());
        orderServiceMapper.updateById(orderInfo);
    }
}
