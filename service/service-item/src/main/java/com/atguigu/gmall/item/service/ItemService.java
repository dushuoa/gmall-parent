package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:12
 * @Version 1.0
 */
public interface ItemService {

    /**
     * 整合sku详情信息接口
     * @param skuId 商品Id
     * @return 详情信息
     */
    Map<String,Object> getSkuItem(Long skuId);
}
