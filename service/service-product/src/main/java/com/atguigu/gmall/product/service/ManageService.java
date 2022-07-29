package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/24 12:20
 * @Version 1.0
 */
public interface ManageService {

    /**
     * 全部一级分类
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
     * 根据平台属性Id 获取到平台属性值集合
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

    /**
     * sku分页列表
     * @param pageParam 分页参数
     * @return sku分页列表
     */
    IPage<SpuInfo> getSpuList(Page<SpuInfo> pageParam);

    /**
     * 品牌 分页列表
     * @param pageParam 分页参数
     * @return 品牌 分页列表
     */
    IPage<BaseTrademark> getBaseTrademarkPage(Page<BaseTrademark> pageParam);

    /**
     * 获取销售属性数据
     * @return 销售属性数据
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spu
     * @param spuInfo spu信息
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId 查询销售属性
     * @param spuId spuId
     * @return 销售属性及属性值
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    /**
     * 根据spuId 获取spuImage 集合
     * @param spuId spuId
     * @return spuImage 集合
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 保存/修改 SkuInfo
     * @param skuInfo sku全部信息
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据三级分类id查询出sku分页列表
     * @param pageParam 分页参数
     * @param skuInfo skuInfo对象
     * @return sku分页列表
     */
    IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> pageParam, SkuInfo skuInfo);

    /**
     * 上架
     * @param skuId 商品id
     */
    void onSale(Long skuId);

    /**
     * 下架
     * @param skuId 商品id
     */
    void cancelSale(Long skuId);

    /**
     * 根据spuId查询出对应的全部信息
     * @param spuId spuId
     * @return 全部信息
     */
    SpuInfo getSpuInfo(Long spuId);

    /**
     * 根据skuId获取sku全部信息
     * @param skuId 商品id
     * @return sku全部信息
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 获取SkuInfo 详细信息，包括图片
     * @param skuId 商品id
     * @return sku全部信息以及sku对应的图片列表
     */
    SkuInfo getApiSkuInfo(Long skuId);

    /**
     * 根据三级分类id，查询对应的一二三级分类信息
     * @param category3Id 三级分类id
     * @return 一二三级分类信息
     */
    BaseCategoryView getCategoryViewByCategory3Id(String category3Id);

    /**
     * 获取价格信息，实时查询
     * @param skuId 商品id
     * @return 价格
     */
    BigDecimal getSkuPriceBySkuId(Long skuId);

    /**
     * 根据spuId 获取海报数据
     * @param spuId spuId
     * @return 海报数据
     */
    List<SpuPoster> getSpuPosterBySpuId(Long spuId);

    /**
     * 根据 skuId和spuId查询出对应的销售属性和销售属性值
     * @param skuId 商品id
     * @param spuId spuId
     * @return 销售属性和销售属性值
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     * 根据spuId 查询该spu下所有sku对应的属性值关系
     * @param spuId spuId
     * @return 所有sku对应的属性值关系
     */
    Map<Object, Object> getSkuValueIdsMap(Long spuId);
}
