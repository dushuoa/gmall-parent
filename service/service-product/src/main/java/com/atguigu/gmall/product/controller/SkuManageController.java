package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/27 18:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;

    // /admin/product/saveSkuInfo
    // 保存SkuInfo
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    // /admin/product/list/{page}/{limit}
    // 根据三级分类id查询出sku分页列表
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuInfoPage(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SkuInfo skuInfo){
        Page<SkuInfo> pageParam = new Page<>(page, limit);
        IPage<SkuInfo> spuInfoPage = manageService.getSkuInfoPage(pageParam,skuInfo);
        return Result.ok(spuInfoPage);
    }

    // /admin/product/onSale/{skuId}
    // 上架
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        manageService.onSale(skuId);
        return Result.ok();
    }

    // /admin/product/cancelSale/{skuId}
    // 下架
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        manageService.cancelSale(skuId);
        return Result.ok();
    }

    // http://localhost/admin/product/getSkuInfo/21
    // 根据skuId获取sku全部信息
    @GetMapping("/getSkuInfo/{skuId}")
    public Result getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        return Result.ok(skuInfo);
    }

    // http://localhost/admin/product/updateSkuInfo
    // 修改sku信息
    @PostMapping("/updateSkuInfo")
    public Result updateSkuInfo(@RequestBody SkuInfo skuInfo){
        // 调用服务层修改sku信息
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }



}
