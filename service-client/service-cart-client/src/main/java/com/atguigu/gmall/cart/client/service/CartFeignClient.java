package com.atguigu.gmall.cart.client.service;

import com.atguigu.gmall.cart.client.service.impl.CartFeignClientImpl;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:15
 * @Version 1.0
 */
@FeignClient(value = "service-cart",fallback = CartFeignClientImpl.class)
public interface CartFeignClient {

    @GetMapping("/api/cart/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable String userId);
}
