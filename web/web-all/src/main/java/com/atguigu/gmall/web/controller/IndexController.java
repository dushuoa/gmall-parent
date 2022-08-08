package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @Author dushuo
 * @Date 2022/8/8 14:43
 * @Version 1.0
 */
@Controller
public class IndexController {

    @Resource
    private ProductFeignClient productFeignClient;

    @GetMapping(value = {"/","/index.html"})
    public String getBaseCategoryList(Model model){
        Result result = productFeignClient.getBaseCategoryList();
        Result bannerResult = productFeignClient.getBannerList();

        model.addAttribute("list",result.getData());
        model.addAttribute("bannerList",bannerResult.getData());
        return "index/index";
    }


}
