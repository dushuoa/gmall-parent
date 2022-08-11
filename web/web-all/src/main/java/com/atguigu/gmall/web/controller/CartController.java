package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:11
 * @Version 1.0
 */
@Controller
public class CartController {

    @Resource
    private ProductFeignClient productFeignClient;

    // 添加购物车
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam(name = "skuId") Long skuId,
                          @RequestParam(name = "skuNum") Integer skuNum,
                          HttpServletRequest request){
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("skuNum",skuNum);
        return "cart/addCart";
    }

    // 展示购物车
    @RequestMapping("cart.html")
    public String showCart(){
        return "cart/index";
    }





}
