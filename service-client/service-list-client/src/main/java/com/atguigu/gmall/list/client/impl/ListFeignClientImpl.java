package com.atguigu.gmall.list.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.stereotype.Service;

/**
 * @Author dushuo
 * @Date 2022/8/5 22:35
 * @Version 1.0
 */
@Service
public class ListFeignClientImpl implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }

    @Override
    public Result search(SearchParam searchParam) {
        return null;
    }

}
