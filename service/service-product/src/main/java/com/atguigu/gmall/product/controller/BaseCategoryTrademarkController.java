package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/7/26 16:50
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {

    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;

    // /admin/product/baseCategoryTrademark/findTrademarkList/{category3Id}
    // 根据category3Id获取品牌列表
    @GetMapping("/findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable Long category3Id){
        List<BaseTrademark> baseTrademarkList = baseCategoryTrademarkService.getTrademarkList(category3Id);
        return Result.ok(baseTrademarkList);
    }

    // /admin/product/baseCategoryTrademark/findCurrentTrademarkList/{category3Id}
    // 根据category3Id获取可选品牌列表
    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList(@PathVariable Long category3Id){
        List<BaseTrademark> baseTrademarkList = baseCategoryTrademarkService.getCurrentTrademarkList(category3Id);
        return Result.ok(baseTrademarkList);
    }

    // /admin/product/baseCategoryTrademark/save
    // 保存分类品牌关联
    @PostMapping("/save")
    public Result saveBaseCategoryTrademark(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.saveBaseCategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }

    // /admin/product/baseCategoryTrademark/remove/{category3Id}/{trademarkId}
    // 删除分类品牌关联
    @DeleteMapping("/remove/{category3Id}/{trademarkId}")
    public Result removeBaseCategoryTrademark(@PathVariable Long category3Id,
                                              @PathVariable Long trademarkId){
        baseCategoryTrademarkService.removeBaseCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }







}
