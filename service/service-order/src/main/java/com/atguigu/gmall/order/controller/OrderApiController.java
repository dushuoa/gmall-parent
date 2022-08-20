package com.atguigu.gmall.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.gmall.cart.client.service.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.service.constant.MqConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.user.client.service.UserFeignClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    @Resource
    private ProductFeignClient productFeignClient;

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

        List<CompletableFuture> completableFutureList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        CompletableFuture<Void> tradeNoCompletableFuture = CompletableFuture.runAsync(() -> {
            // 获取流水号
            String tradeNo = request.getParameter("tradeNo");
            // 判断用户传过来的和redis是否相等
            Boolean result = orderService.isTradeNoEqualsRedis(userId, tradeNo);
            if (!result) {
                errorList.add("请刷新后再试！");
            }
            // 如果相等，删除redis
            orderService.deleteRedis(userId);
        });

        completableFutureList.add(tradeNoCompletableFuture);

        // 查询库存系统,循环判断每个商品是否有库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
                Long skuId = orderDetail.getSkuId();
                Integer skuNum = orderDetail.getSkuNum();
                boolean res = orderService.checkStock(skuId, skuNum);
                if (!res) {
                    // 如果没有库存，提示扣减库存失败
                    errorList.add(orderDetail.getSkuName() + "库存扣减失败！");
                }
                // 获取数据库的实时价格
                BigDecimal currentPrice = productFeignClient.getSkuPriceBySkuId(skuId);
                // 获取当前提交的购物项的价格
                BigDecimal orderPrice = orderDetail.getOrderPrice();
                // 如果两个价格不相等
                if (currentPrice.compareTo(orderPrice) != 0) {
                    String msg = currentPrice.compareTo(orderPrice) > 0 ? "涨价" : "降价";
                    BigDecimal abs = currentPrice.subtract(orderPrice).abs();
                    errorList.add(orderDetail.getSkuName() + msg + abs + "元");
                }
            });
            completableFutureList.add(priceCompletableFuture);
        }
        // 验证通过，保存订单
        CompletableFuture
                .allOf(completableFutureList
                        .toArray(new CompletableFuture[completableFutureList.size()]))
                .join();
        if(errorList.size()>0){
            return Result.fail().message(StringUtils.join(errorList,","));
        }
        orderInfo.setUserId(Long.parseLong(userId));

        Long orderId = orderService.submitOrder(orderInfo);
        return Result.ok(orderId);
    }

    // 我的订单
    // /api/order/auth/{page}/{limit}
    @GetMapping("/auth/{page}/{limit}")
    public Result getMyOrder(@PathVariable Long page,
                             @PathVariable Long limit,
                             HttpServletRequest request){
        // 获取用户Id
        String userId = AuthContextHolder.getUserId(request);
        // 封装分页参数
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageResult = orderService.getMyOrderPage(pageParam,userId);
        return Result.ok(pageResult);
    }

    // 根据订单id获取订单详细数据
    @GetMapping("/auth/comment/{orderId}")
    public OrderInfo getCommentInfo(@PathVariable Long orderId){
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        return orderInfo;
    }

    // 拆分订单
    @PostMapping("/orderSplit")
    public String orderSplit(HttpServletRequest request){
        // 获取请求参数
        String orderId = request.getParameter("orderId");
        // [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        String wareSkuMap = request.getParameter("wareSkuMap");
        List<OrderInfo> list = orderService.orderSplit(Long.parseLong(orderId),wareSkuMap);
        // 封装数据返回
        List<Map<String, Object>> mapList = list.stream().map(orderInfo -> {
            Map<String, Object> map = orderService.getMapByOrderInfo(orderInfo);
            return map;
        }).collect(Collectors.toList());
        return JSON.toJSONString(mapList);
    }


}
