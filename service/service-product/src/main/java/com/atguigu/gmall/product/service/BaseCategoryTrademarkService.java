package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/26 16:52
 * @Version 1.0
 */
public interface BaseCategoryTrademarkService extends IService<BaseCategoryTrademark> {
    /**
     * 根据category3Id获取品牌列表
     * @param category3Id 三级分类Id
     * @return 品牌列表
     */
    List<BaseTrademark> getTrademarkList(Long category3Id);

    /**
     * 根据category3Id获取可选品牌列表
     * @param category3Id 三级分类Id
     * @return 可选品牌列表
     */
    List<BaseTrademark> getCurrentTrademarkList(Long category3Id);

    /**
     * 保存分类品牌关联
     * @param categoryTrademarkVo 要保存的品牌
     */
    void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);

    /**
     * 删除分类品牌关联
     * @param category3Id 三级分类Id
     * @param trademarkId 删除对应关联的品牌Id
     */
    void removeBaseCategoryTrademark(Long category3Id, Long trademarkId);
}
