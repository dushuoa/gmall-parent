package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author dushuo
 * @Date 2022/7/26 16:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {


    @Autowired
    private BaseTrademarkService baseTrademarkService;


    // /admin/product/baseTrademark/{page}/{limit}
    // 品牌 分页列表
    @GetMapping("/{page}/{limit}")
    public Result getBaseTrademarkPage(@PathVariable Long page,
                                       @PathVariable Long limit){
        Page<BaseTrademark> pageParam = new Page<>(page, limit);
        Page<BaseTrademark> pageResult = baseTrademarkService.page(pageParam, null);
        return Result.ok(pageResult);
    }

    // /admin/product/baseTrademark/save
    // 保存品牌
    @PostMapping("/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    // /admin/product/baseTrademark/remove/{id}
    @DeleteMapping("/remove/{id}")
    public Result removeBaseTrademark(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    // /admin/product/baseTrademark/get/{id}
    // 根据品牌Id 回显品牌数据 --- 获取详情
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    // /admin/product/baseTrademark/update
    // 修改品牌信息
    @PutMapping("/update")
    public Result updateById(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

}
