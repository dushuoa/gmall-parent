package com.atguigu.gmall.product.api;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuPoster;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/29 16:48
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;

    // 获取SkuInfo 详细信息，包括图片
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo = manageService.getApiSkuInfo(skuId);
        return skuInfo;
    }

    // 根据三级分类id，查询对应的一二三级分类信息
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewByCategory3Id(@PathVariable String category3Id){
        return manageService.getCategoryViewByCategory3Id(category3Id);
    }


    // 获取价格信息，实时查询
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPriceBySkuId(@PathVariable Long skuId){
        return manageService.getSkuPriceBySkuId(skuId);
    }

    //  根据spuId 获取海报数据
    @GetMapping("inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> getSpuPosterBySpuId(@PathVariable Long spuId){
        return manageService.getSpuPosterBySpuId(spuId);
    }

    // 根据 skuId和spuId查询出对应的销售属性和销售属性值
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId){
        return manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    // 根据spuId 查询该spu下所有sku对应的属性值关系
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map<Object,Object> getSkuValueIdsMap(@PathVariable Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }

}
