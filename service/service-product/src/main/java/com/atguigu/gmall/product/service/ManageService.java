package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/24 12:20
 * @Version 1.0
 */
public interface ManageService {

    /**
     * @return 全部一级分类
     */
    List<BaseCategory1> getCategory1();

    /**
     * 查询对应一级分类的二级分类
     * @param category1Id 一级分类id
     * @return 对应的二级分类
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级分类id查询出对应的三级分类
     * @param category2Id 二级id
     * @return 对应的三级分类
     */
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 根据分类Id 获取平台属性集合
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return 根据分类Id 获取平台属性集合
     */
    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存/修改 平台属性和属性值
     * @param baseAttrInfo 平台属性和属性值对象
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * @param attrId 平台属性Id
     * @return 根据平台属性Id 获取到平台属性值集合
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     * 根据属性Id获取对应的全部信息，包括平台属性值
     * @param attrId 平台属性Id
     * @return 属性Id对应的全部信息
     */
    BaseAttrInfo getBaseAttrInfoById(Long attrId);
}
