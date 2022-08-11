package com.atguigu.gmall.order.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author dushuo
 * @Date 2022/8/10 18:40
 * @Version 1.0
 */
@Component
public class OrderFeignClientImpl implements OrderFeignClient {
    @Override
    public Result trade() {
        return null;
    }
}
