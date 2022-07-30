package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/item")
public class ItemApuController {

    @Autowired
    private ItemService itemService;

    // 整合sku详情信息接口
    @GetMapping("{skuId}")
    public Result getSkuItem(@PathVariable Long skuId){
        Map<String,Object> map = itemService.getSkuItem(skuId);
        return Result.ok(map);
    }

}
