package com.atguigu.gmall.list.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.impl.ListFeignClientImpl;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author dushuo
 * @Date 2022/8/5 22:34
 * @Version 1.0
 */
@FeignClient(value = "service-list",fallback = ListFeignClientImpl.class)
public interface ListFeignClient {
    /**
     * 更新商品incrHotScore
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("api/list/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 上架商品
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("api/list/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 下架商品
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("api/list/inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 根据搜索参数查询结果列表
     * @param searchParam 搜索参数
     * @return 结果
     */
    @PostMapping("api/list")
    public Result search(@RequestBody SearchParam searchParam);
}
