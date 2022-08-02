package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:13
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedissonClient redissonClient;

    // 整合sku详情信息接口
    @Override
    public Map<String,Object> getSkuItem(Long skuId) {
        // 先去判断布隆过滤器有没有
        // 方便后续测试 先注释
//        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
//        if(!bloomFilter.contains(skuId)){
//            return null;
//        }

        Map<String, Object> resultMap = new HashMap<>();

        // 获取sku基本信息和图片信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 获取分类信息
        BaseCategoryView categoryView = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());

        // 获取价格信息
        BigDecimal price = productFeignClient.getSkuPriceBySkuId(skuId);

        // 获取海报信息
        List<SpuPoster> spuPosterList = productFeignClient.getSpuPosterBySpuId(skuInfo.getSpuId());

        // 获取销售属性信息
        List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());

        // 获取销售属性值及对应的skuId 需要转化为Json
        Map<Object, Object> skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);

        // 获取Sku对应的平台属性
        List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
        // 平台属性只需要属性名和属性值
        List<Map> skuAttrList = new ArrayList<>();
        attrList.forEach(baseAttrInfo -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("attrName",baseAttrInfo.getAttrName());
            map.put("attrValue",baseAttrInfo.getAttrValueList().get(0).getValueName());
            skuAttrList.add(map);
        });

        resultMap.put("skuInfo",skuInfo);
        resultMap.put("categoryView",categoryView);
        resultMap.put("price",price);
        resultMap.put("spuPosterList",spuPosterList);
        resultMap.put("spuSaleAttrList",spuSaleAttrList);
        resultMap.put("valuesSkuJson",valuesSkuJson);
        resultMap.put("skuAttrList",skuAttrList);
        return resultMap;
    }
}
