package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/26 16:14
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class SpuManageController {

    @Autowired
    private ManageService manageService;

    // /admin/product/list/{page}/{limit}
    // sku分页列表
    @GetMapping("/{page}/{limit}")
    public Result getSpuList(@PathVariable Long page,
                             @PathVariable Long limit){
        Page<SpuInfo> pageParam = new Page<SpuInfo>(page, limit);
        IPage<SpuInfo> pageResult = manageService.getSpuList(pageParam);
        return Result.ok(pageResult);
    }

    // /admin/product/baseSaleAttrList
    // 获取销售属性数据
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }









}
