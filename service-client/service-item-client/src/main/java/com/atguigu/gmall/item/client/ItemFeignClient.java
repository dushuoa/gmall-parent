package com.atguigu.gmall.item.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.impl.ItemFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:42
 * @Version 1.0
 */
@FeignClient(value = "service-item",fallback = ItemFeignClientImpl.class)
public interface ItemFeignClient {

    @GetMapping("/api/item/{skuId}")
    public Result getSkuItem(@PathVariable Long skuId);

}
