package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/27 18:33
 * @Version 1.0
 * 销售属性值
 */
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据skuId查询出对应的销售属性和销售属性值
     * @param skuId skuId
     * @return 销售属性和销售属性值
     */
    List<SkuSaleAttrValue> selectSkuSaleAttrValueList(Long skuId);
}
