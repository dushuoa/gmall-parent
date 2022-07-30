package com.atguigu.gmall.product.client.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:20
 * @Version 1.0
 */
@Service
public class ProductFeignClientImpl implements ProductFeignClient {
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getSkuPriceBySkuId(Long skuId) {
        return null;
    }

    @Override
    public List<SpuPoster> getSpuPosterBySpuId(Long spuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return null;
    }

    @Override
    public Map<Object, Object> getSkuValueIdsMap(Long spuId) {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return null;
    }
}
