package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BannerInfo;
import com.atguigu.gmall.product.service.BannerService;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.List;

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
    @Resource
    private BannerService bannerService;

    // 获取banner分页列表
    @GetMapping("{page}/{limit}")
    public Result getBannerPage(@PathVariable Long limit,
                                @PathVariable Long page
                                ){
        Page<BannerInfo> pageParam = new Page(page,limit);
        IPage<BannerInfo> pageResult = manageService.getBannerPage(pageParam);
        return Result.ok(pageResult);
    }

    // 删除
    @DeleteMapping("/remove/{id}")
    public Result deleteBanner(@PathVariable Long id){
        bannerService.removeById(id);
        return Result.ok();
    }

    // 根据id获取
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        BannerInfo bannerInfo = bannerService.getById(id);
        return Result.ok(bannerInfo);
    }

    // 修改
    @PutMapping("/update")
    public Result updateBanner(@RequestBody BannerInfo bannerInfo){
        bannerService.updateById(bannerInfo);
        return Result.ok();
    }

    // 新增
    @PostMapping("/save")
    public Result save(@RequestBody BannerInfo bannerInfo){
        bannerService.save(bannerInfo);
        return Result.ok();
    }

    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        bannerService.removeByIds(idList);
        return Result.ok();
    }

    // 获取前三个添加的轮播图
    @GetMapping("/getBannerList")
    public Result getBannerList(){
        List<BannerInfo> bannerInfoList = bannerService.getBannerList();
        return Result.ok(bannerInfoList);
    }



}
