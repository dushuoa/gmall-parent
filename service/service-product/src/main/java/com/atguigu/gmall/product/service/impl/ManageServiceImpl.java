package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/24 12:21
 * @Version 1.0
 */
@Service
public class ManageServiceImpl implements ManageService {
    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuPosterMapper spuPosterMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    //查询全部一级分类
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    //查询对应一级分类的二级分类
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        wrapper.eq("is_deleted",0);
        return baseCategory2Mapper.selectList(wrapper);
    }

    //根据二级分类id查询出对应的三级分类
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",category2Id);
        wrapper.eq("is_deleted",0);
        return baseCategory3Mapper.selectList(wrapper);
    }

    //根据分类Id 获取平台属性集合
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.getAttrInfoList(category1Id,category2Id,category3Id);
    }

    //保存/修改 平台属性和属性值
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        /*
         * 保存平台属性信息
         * 分别保存到 base_attr_info 和 base_attr_value
         */

        //修改平台属性信息,判断传过来的对象有没有id，有就修改，没有则新增
        if(baseAttrInfo.getId() != null){
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //修改平台属性值，我们无法确定用户到底是删除还是新增，所以先删除，后新增
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            wrapper.eq("is_deleted",0);
            baseAttrValueMapper.delete(wrapper);
        }else {
            //新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        attrValueList.forEach(baseAttrValue -> {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        });


    }

    //根据平台属性Id 获取到平台属性值集合
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",attrId);
        wrapper.eq("is_deleted",0);
        return baseAttrValueMapper.selectList(wrapper);
    }

    //根据属性Id获取对应的全部信息，包括平台属性值
    @Override
    public BaseAttrInfo getBaseAttrInfoById(Long attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if(baseAttrInfo == null){
            throw new RuntimeException("平台属性信息有误");
        }
        List<BaseAttrValue> attrValueList = this.getAttrValueList(baseAttrInfo.getId());
        baseAttrInfo.setAttrValueList(attrValueList);
        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> getSpuList(Page<SpuInfo> pageParam) {
        return spuInfoMapper.selectPage(pageParam,null);
    }

    // 品牌 分页列表
    @Override
    public IPage<BaseTrademark> getBaseTrademarkPage(Page<BaseTrademark> pageParam) {
        return baseTrademarkMapper.selectPage(pageParam,null);
    }

    // 销售属性数据
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    // 保存spu
    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        // 判断传过来的对象是否有id属性，如果有则修改，没有则新增
        if(spuInfo.getId() != null){
            Long spuId = spuInfo.getId();
            //修改
            // 我们不知道用户是删除了图片/销售属性/销售属性值还是新增
            // 所以我们应该先删除在新增，直接全部删除
            spuInfoMapper.updateById(spuInfo);
            // 删除spu对应的图片/销售属性/销售属性值,
            // 不需要删除对应的sku，因为这个接口不涉及对sku的操作
            spuImageMapper.delete(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
            spuPosterMapper.delete(new QueryWrapper<SpuPoster>().eq("spu_id",spuId));
            spuSaleAttrMapper.delete(new QueryWrapper<SpuSaleAttr>().eq("spu_id",spuId));
            spuSaleAttrValueMapper.delete(new QueryWrapper<SpuSaleAttrValue>().eq("spu_id",spuId));
            //在新增
        }else {
            // 插入spu基本信息，获取自增的主键
            spuInfoMapper.insert(spuInfo);
        }

        //获取spu图片集合
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(!CollectionUtils.isEmpty(spuImageList)){
            spuImageList.forEach(spuImage -> {
                // 补全数据
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }
        //获取海报集合
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        if(!CollectionUtils.isEmpty(spuPosterList)){
            spuPosterList.forEach(spuPoster -> {
                spuPoster.setSpuId(spuInfo.getId());
                spuPosterMapper.insert(spuPoster);
            });
        }
        //获取销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(!CollectionUtils.isEmpty(spuSaleAttrList)){
            spuSaleAttrList.forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                // 销售属性值集合
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    spuSaleAttrValueList.forEach(spuSaleAttrValue -> {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    });
                }
            });
        }
    }

    // 根据spuId 查询销售属性及属性值
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    // 根据spuId 获取spuImage 集合
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        return spuImageMapper.selectList(wrapper);
    }

    // 保存/修改 SkuInfo
    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        if(skuInfo.getId()!=null){
            // 不是null，说明是修改
            // 根据id修改spuInfo
            skuInfoMapper.updateById(skuInfo);
            // 删除关于该sku的其他信息
            // 删除图片
            skuImageMapper.delete(new QueryWrapper<SkuImage>().eq("sku_id",skuInfo.getId()));
            // 删除平台属性
            skuAttrValueMapper.delete(new QueryWrapper<SkuAttrValue>().eq("sku_id",skuInfo.getId()));
            // 删除销售属性
            skuSaleAttrValueMapper.delete(new QueryWrapper<SkuSaleAttrValue>().eq("sku_id",skuInfo.getId()));
        }else {
            //插入skuInfo
            skuInfoMapper.insert(skuInfo);
        }

        //分别取出，插入到不同的表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            skuImageList.forEach(skuImage -> {
                // 补全值
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            });
        }
        //获取平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            skuAttrValueList.forEach(skuAttrValue -> {
                // 补全值
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            });
        }

        // 获取销售属性及属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            skuSaleAttrValueList.forEach(skuSaleAttrValue -> {
                // 补全值
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            });
        }
    }

    // 根据三级分类id查询出sku分页列表
    @Override
    public IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> pageParam, SkuInfo skuInfo) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",skuInfo.getCategory3Id());
        return skuInfoMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public SpuInfo getSpuInfo(Long spuId) {
        // 查询spuInfo，获取对应的spu信息
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);

        //查询销售属性集合
        List<SpuSaleAttr> spuSaleAttrList =
                spuSaleAttrMapper.selectSpuSaleAttrList(spuId);

        //获取spu图片列表
        List<SpuImage> spuImageList =
                spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));

        //获取spu海报列表
        List<SpuPoster> spuPosterList =
                spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuId));

        // 封装到对象中
        spuInfo.setSpuSaleAttrList(spuSaleAttrList);
        spuInfo.setSpuImageList(spuImageList);
        spuInfo.setSpuPosterList(spuPosterList);

        return spuInfo;
    }

    // 根据skuId获取sku全部信息
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        // 查询对应的商品详情信息

        //  sku_info
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //  sku_image
        List<SkuImage> skuImageList = skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id", skuId));

        //  sku_attr_value 根据skuId获取平台属性和属性值
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.selectSkuAttrValueList(skuId);

        //  sku_sale_attr_value 根据skuId查询出对应的销售属性和销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.selectSkuSaleAttrValueList(skuId);

        //放入数据
        skuInfo.setSkuImageList(skuImageList);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);

        return skuInfo;
    }
}
