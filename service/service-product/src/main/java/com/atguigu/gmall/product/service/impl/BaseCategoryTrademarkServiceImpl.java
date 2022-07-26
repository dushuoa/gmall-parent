package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author dushuo
 * @Date 2022/7/26 16:52
 * @Version 1.0
 */
@Service
public class BaseCategoryTrademarkServiceImpl
        extends ServiceImpl<BaseCategoryTrademarkMapper,BaseCategoryTrademark>
        implements BaseCategoryTrademarkService {

    @Resource
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;


    //根据category3Id获取品牌列表
    @Override
    public List<BaseTrademark> getTrademarkList(Long category3Id) {
        // 先通过中间表查询出三级分类Id对应的信息
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        wrapper.orderByDesc("id");
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            //然后取出其中的图片id
            List<Long> trademarkIdList =
                    baseCategoryTrademarkList
                            .stream()
                            .map(BaseCategoryTrademark::getTrademarkId)
                            .collect(Collectors.toList());
            //批量查询
            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectBatchIds(trademarkIdList);
            return baseTrademarkList;
        }

        return null;
    }

    //根据category3Id获取可选品牌列表
    @Override
    public List<BaseTrademark> getCurrentTrademarkList(Long category3Id) {
        // 先通过中间表查询出三级分类Id对应的信息
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            //然后取出其中的品牌id 已经有的
            List<Long> trademarkIdList =
                    baseCategoryTrademarkList
                            .stream()
                            .map(BaseCategoryTrademark::getTrademarkId)
                            .collect(Collectors.toList());
            //查询所有品牌属性，获取id
            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null);
            // 获取该三级分类没有的品牌id
            List<Long> ids = baseTrademarkList.stream()
                    .map(BaseTrademark::getId)
                    .filter(id -> !trademarkIdList.contains(id))
                    .collect(Collectors.toList());

            //查询出对应的品牌属性
            return baseTrademarkMapper.selectBatchIds(ids);
        }
        return null;
    }

    //保存分类品牌关联
    @Override
    public void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        //品牌id的集合
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        if(!CollectionUtils.isEmpty(trademarkIdList)){
            //批量插入数据
            ArrayList<BaseCategoryTrademark> categoryTrademarkArrayList = new ArrayList<>();
            trademarkIdList.forEach(id->{
                BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
                baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
                baseCategoryTrademark.setTrademarkId(id);
                categoryTrademarkArrayList.add(baseCategoryTrademark);
            });
            new BaseCategoryTrademarkServiceImpl().saveBatch(categoryTrademarkArrayList);
        }
    }

    //删除分类品牌关联
    @Override
    public void removeBaseCategoryTrademark(Long category3Id, Long trademarkId) {
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        wrapper.eq("trademark_id",trademarkId);
        baseCategoryTrademarkMapper.delete(wrapper);
    }




}
