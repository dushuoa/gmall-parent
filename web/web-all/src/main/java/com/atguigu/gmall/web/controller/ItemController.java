package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/7/30 16:51
 * @Version 1.0
 *
 * 商品详情
 */
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private TemplateEngine templateEngine;

    @GetMapping("{skuId}.html")
    public String getSkuItem(@PathVariable Long skuId, Model model){
        Result result = itemFeignClient.getSkuItem(skuId);
        model.addAllAttributes((Map) result.getData());
        return "item/item";
    }

    @GetMapping(value = {"/","/index.html"})
    public String getBaseCategoryList(Model model){
        Result result = productFeignClient.getBaseCategoryList();
        model.addAttribute("list",result.getData());
        return "index/index";
    }
    @GetMapping("create")
    @ResponseBody
    public Result createIndex(){
        Result result = productFeignClient.getBaseCategoryList();
        Context context = new Context();
        context.setVariable("list",result.getData());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("E:\\software\\nginx-1.12.0\\html\\index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        templateEngine.process("index/index.html",context,fileWriter);
        return Result.ok();
    }


}
