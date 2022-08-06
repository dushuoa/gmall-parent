package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.io.IOException;

/**
 * @Author dushuo
 * @Date 2022/8/3 21:40
 * @Version 1.0
 */
public interface SearchService {

    /**
     * 上传商品
     * @param skuId 商品id
     */
    void upperGoods(Long skuId);

    /**
     * 下架商品
     * @param skuId 商品id
     */
    void lowerGoods(Long skuId);

    /**
     * 更新热点
     * @param skuId 商品id
     */
    void incrHotScore(Long skuId);

    /**
     * 根据搜索参数查询结果列表
     * @param searchParam 搜索参数
     * @return 结果
     * @throws IOException IO异常
     */
    SearchResponseVo search(SearchParam searchParam) throws IOException;


}
