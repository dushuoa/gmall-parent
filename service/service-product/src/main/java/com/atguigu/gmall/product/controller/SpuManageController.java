package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
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

    // /admin/product/saveSpuInfo
    // 保存spu
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    // /admin/product/spuSaleAttrList/{spuId}
    // 根据spuId 查询销售属性
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }

    // /admin/product/spuImageList/{spuId}
    // 根据spuId 获取spuImage 集合
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    // http://localhost/admin/product/getSpuInfo/6
    // 根据spuId查询出对应的全部信息
    @GetMapping("/getSpuInfo/{spuId}")
    public Result getSpuInfo(@PathVariable Long spuId){
        SpuInfo spuInfo = manageService.getSpuInfo(spuId);
        return Result.ok(spuInfo);
    }

    // http://localhost/admin/product/updateSpuInfo
    // 修改spuInfo属性值
    @PostMapping("/updateSpuInfo")
    public Result updateSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }


}
