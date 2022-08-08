package com.atguigu.gmall.product.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.impl.ProductFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:19
 * @Version 1.0
 */
@FeignClient(value = "service-product",fallback = ProductFeignClientImpl.class)
public interface ProductFeignClient {


    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);

    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewByCategory3Id(@PathVariable Long category3Id);

    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPriceBySkuId(@PathVariable Long skuId);

    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> getSpuPosterBySpuId(@PathVariable Long spuId);


    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId);

    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map<Object,Object> getSkuValueIdsMap(@PathVariable Long spuId);

    // 根据skuId查询对应的平台属性
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId);

    @GetMapping("/api/product/getBaseCategoryList")
    public Result getBaseCategoryList();

    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);

    // 获取前三个添加的轮播图
    @GetMapping("/admin/product/banner/getBannerList")
    public Result getBannerList();
}
