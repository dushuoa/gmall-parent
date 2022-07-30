package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/24 12:24
 * @Version 1.0
 */
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    //根据分类Id 获取平台属性集合
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    // 根据skuId查询对应的平台属性
    List<BaseAttrInfo> selectAttrList(Long skuId);
}
