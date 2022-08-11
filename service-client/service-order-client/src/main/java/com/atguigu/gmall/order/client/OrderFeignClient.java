package com.atguigu.gmall.order.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.client.impl.OrderFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author dushuo
 * @Date 2022/8/10 18:40
 * @Version 1.0
 */
@FeignClient(value = "service-order",fallback = OrderFeignClientImpl.class)
public interface OrderFeignClient {

    @GetMapping("/api/order/auth/trade")
    public Result trade();
}
