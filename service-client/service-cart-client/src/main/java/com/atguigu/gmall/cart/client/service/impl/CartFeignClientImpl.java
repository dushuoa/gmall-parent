package com.atguigu.gmall.cart.client.service.impl;

import com.atguigu.gmall.cart.client.service.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:16
 * @Version 1.0
 */
@Component
public class CartFeignClientImpl implements CartFeignClient {
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        return null;
    }
}
