package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
        rabbitService.sendDelay(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL,orderInfo.getId(),MqConst.DELAY_TIME);
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

    public void updateOrderStatusByOrderId(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setProcessStatus(processStatus.name());
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setUpdateTime(new Date());
        orderServiceMapper.updateById(orderInfo);
    }

    // 支付成功后，扣减库存
    @Override
    public void subWare(Long orderId) {
        OrderInfo orderInfo = this.getOrderInfo(orderId);

        // 转换为json
        Map<String, Object> map = this.getMapByOrderInfo(orderInfo);

        // 发送json消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK,MqConst.ROUTING_WARE_STOCK, JSON.toJSONString(map));
        // 已通知仓库
        this.updateOrderStatusByOrderId(orderId,ProcessStatus.NOTIFIED_WARE);
    }

    // 拆分订单
    @Override
    @Transactional
    public List<OrderInfo> orderSplit(long orderId, String wareSkuMap) {
        // 拆分后的订单信息集合
        ArrayList<OrderInfo> orderInfoArrayList = new ArrayList<>();

        OrderInfo orderInfoOrigin = this.getOrderInfo(orderId);
        // 获取json串 [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        List<Map> mapList = JSON.parseArray(wareSkuMap, Map.class);
        if(!CollectionUtils.isEmpty(mapList)){
            // 遍历这个mapList
            for (Map map : mapList) {
                // 取出数据
                String wareId = (String) map.get("wareId");
                List<String> skuIds = (List<String>) map.get("skuIds");
                OrderInfo subOrderInfo = new OrderInfo();
                // 拷贝属性
                BeanUtils.copyProperties(orderInfoOrigin,subOrderInfo);
                // 防止主键冲突
                subOrderInfo.setId(null);
                // 设置仓库Id
                subOrderInfo.setWareId(wareId);
                // 取出该订单Id对应的订单明细集合
                List<OrderDetail> orderDetailList = orderInfoOrigin.getOrderDetailList();
                // 如果该订单明细对应的skuId，和该循环遍历的仓库对应的skuId一致的话，则放入集合中
                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetailList) {
                    for (String skuId : skuIds) {
                        if(orderDetail.getSkuId().compareTo(Long.parseLong(skuId)) == 0){
                            // 如果一样的话，直接添加到集合中
                            orderDetails.add(orderDetail);
                        }
                    }
                }
                // 把该订单对应的数据放到OrderInfo中
                subOrderInfo.setOrderDetailList(orderDetails);
                // 计算每个订单的金额
                subOrderInfo.sumTotalAmount();
                // 保存父id
                subOrderInfo.setParentOrderId(orderId);
                // 保存子订单到订单表
                orderInfoMapper.insert(subOrderInfo);
                // 把该笔子订单封装到list返回
                orderInfoArrayList.add(subOrderInfo);
            }
            // 修改原始订单的状态
            orderInfoOrigin.setOrderStatus(OrderStatus.SPLIT.name());
            orderInfoMapper.updateById(orderInfoOrigin);
            return orderInfoArrayList;
        }
        return null;
    }

    // 支付成功后，扣减库存
    public Map<String, Object> getMapByOrderInfo(OrderInfo orderInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderInfo.getId());
        map.put("consignee", orderInfo.getConsignee());
        map.put("consigneeTel", orderInfo.getConsigneeTel());
        map.put("orderComment", orderInfo.getOrderComment());
        map.put("orderBody", orderInfo.getTradeBody());
        map.put("deliveryAddress", orderInfo.getDeliveryAddress());
        map.put("paymentWay", "2");
        map.put("wareId", orderInfo.getWareId());// 仓库Id ，减库存拆单时需要使用！
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if(!CollectionUtils.isEmpty(orderDetailList)){
            List<HashMap<String, Object>> list = orderDetailList.stream().map(orderDetail -> {
                HashMap<String, Object> orderDetailMap = new HashMap<>();
                orderDetailMap.put("skuId", orderDetail.getSkuId());
                orderDetailMap.put("skuNum", orderDetail.getSkuNum());
                orderDetailMap.put("skuName", orderDetail.getSkuName());
                return orderDetailMap;
            }).collect(Collectors.toList());
            map.put("details",list);
        }
        return map;
    }

    @Override
    public void execExpiredOrder(Long orderId,String flag) {
        // 调用方法 状态
        updateOrderStatusByOrderId(orderId,ProcessStatus.CLOSED);
        if ("2".equals(flag)){
            // 发送消息队列，关闭支付宝的交易记录。
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE,MqConst.ROUTING_PAYMENT_CLOSE,orderId);
        }
    }

}
