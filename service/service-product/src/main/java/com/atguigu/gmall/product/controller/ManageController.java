package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/24 11:07
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class ManageController {

    @Autowired
    private ManageService manageService;


    //查询全部一级分类
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    // /admin/product/getCategory2/{category1Id}
    //查询对应一级分类的二级分类
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    ///admin/product/getCategory3/{category2Id}
    //根据二级分类id查询出对应的三级分类
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    //  /admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
    //根据分类Id 获取平台属性集合
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList = manageService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfoList);
    }

    // /admin/product/saveAttrInfo
    // 保存平台属性和属性值
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    // /admin/product/getAttrValueList/{attrId}
    // 根据平台属性Id 获取到平台属性值集合
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        //先要判断有没有这个属性
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfoById(attrId);
        return Result.ok(baseAttrInfo.getAttrValueList());
    }


}
