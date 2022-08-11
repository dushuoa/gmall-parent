package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.service.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.user.client.service.UserFeignClient;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author dushuo
 * @Date 2022/8/10 17:06
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CartFeignClient cartFeignClient;

    @Resource
    private OrderService orderService;

    // 回显订单界面数据
    @GetMapping("auth/trade")
    public Result trade(HttpServletRequest request){
        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        // 放到map返回
        HashMap<String, Object> map = new HashMap<>();
        // 获取用户地址信息
        Result userResult = userFeignClient.findUserAddressListByUserId(userId);

        // 获取订单详情数据 购物项
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        // 转换为orderDetail对象
        AtomicReference<Integer> totalNum = new AtomicReference<>(0);
        List<OrderDetail> detailArrayList = cartCheckedList.stream().map(cartInfo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            totalNum.updateAndGet(v -> v + cartInfo.getSkuNum());
            return orderDetail;
        }).collect(Collectors.toList());

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(detailArrayList);
        orderInfo.sumTotalAmount();

        String tradeNo = orderService.getTradeNo(userId);

        map.put("userAddressList",userResult.getData());
        map.put("detailArrayList",detailArrayList);
        map.put("totalNum",totalNum);
        map.put("totalAmount",orderInfo.getTotalAmount());
        map.put("tradeNo",tradeNo);
        return Result.ok(map);
    }

    // /api/order/auth/submitOrder?tradeNo=null
    // 保存订单数据
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo,
                              HttpServletRequest request){

        String userId = AuthContextHolder.getUserId(request);

        // 获取流水号
        String tradeNo = request.getParameter("tradeNo");
        // 判断用户传过来的和redis是否相等
        Boolean result = orderService.isTradeNoEqualsRedis(userId, tradeNo);
        if(!result){
            return Result.fail().message("请刷新后再试！");
        }
        // 如果相等，删除redis
        orderService.deleteRedis(userId);

        // 查询库存系统,循环判断每个商品是否有库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            Long skuId = orderDetail.getSkuId();
            Integer skuNum = orderDetail.getSkuNum();
            boolean res = orderService.checkStock(skuId, skuNum);
            if(!res){
                // 如果没有库存，提示扣减库存失败
                return Result.fail().message(orderDetail.getSkuName()+"库存扣减失败！");
            }
        }
        // 验证通过，保存订单
        orderInfo.setUserId(Long.parseLong(userId));
        Long orderId = orderService.submitOrder(orderInfo);

        return Result.ok(orderId);
    }


}
