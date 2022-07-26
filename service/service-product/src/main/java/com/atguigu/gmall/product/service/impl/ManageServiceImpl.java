package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
