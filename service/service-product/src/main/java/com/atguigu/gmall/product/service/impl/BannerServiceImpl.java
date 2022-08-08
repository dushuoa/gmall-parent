package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BannerInfo;
import com.atguigu.gmall.product.mapper.BannerInfoMapper;
import com.atguigu.gmall.product.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/8 14:22
 * @Version 1.0
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerInfoMapper, BannerInfo> implements BannerService {

    // 获取前三个添加的轮播图
    @Override
    public List<BannerInfo> getBannerList() {
        QueryWrapper<BannerInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        Page<BannerInfo> pageParam = new Page<>(1, 3);
        Page<BannerInfo> bannerInfoPage = baseMapper.selectPage(pageParam, wrapper);
        return bannerInfoPage.getRecords();
    }
}
