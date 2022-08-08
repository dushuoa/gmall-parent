package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BannerInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/8 14:22
 * @Version 1.0
 */
public interface BannerService extends IService<BannerInfo> {
    /**
     * 获取前三个添加的轮播图
     * @return 轮播图
     */
    List<BannerInfo> getBannerList();


}
