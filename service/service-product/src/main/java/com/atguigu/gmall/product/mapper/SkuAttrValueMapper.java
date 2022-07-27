package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/27 18:28
 * @Version 1.0
 *
 * 平台属性
 */
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
    /**
     * 根据skuId获取平台属性和属性值
     * @param skuId skuId
     * @return 平台属性和属性值
     */
    List<SkuAttrValue> selectSkuAttrValueList(Long skuId);
}
