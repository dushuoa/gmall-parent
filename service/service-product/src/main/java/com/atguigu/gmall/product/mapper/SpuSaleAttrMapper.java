package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/27 16:42
 * @Version 1.0
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 根据spuId 查询销售属性
     * @param spuId spuId
     * @return 销售属性及属性值
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(Long spuId);
}
