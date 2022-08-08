package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BannerInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author dushuo
 * @Date 2022/8/7 12:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/banner")
public class BannerController {

    @Resource
    private ManageService manageService;

    // 获取banner分页列表
    @GetMapping("{page}/{limit}")
    public Result getBannerPage(@PathVariable Long limit,
                                @PathVariable Long page
                                ){
        Page<BannerInfo> pageParam = new Page(page,limit);
        IPage<BannerInfo> pageResult = manageService.getBannerPage(pageParam);
        return Result.ok(pageResult);
    }

}
