package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:51
 * @Version 1.0
 */
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @GetMapping("{skuId}.html")
    public String getSkuItem(@PathVariable Long skuId, Model model){
        Result result = itemFeignClient.getSkuItem(skuId);
        model.addAllAttributes((Map) result.getData());
        return "item/item";
    }

}
