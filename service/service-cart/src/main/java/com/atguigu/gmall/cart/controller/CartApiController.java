package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/9 18:40
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    //添加购物车
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum,
                            HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if(StringUtils.isEmpty(userId)){
            // 如果用户id为空，取出用户临时id
            String userTempId = AuthContextHolder.getUserTempId(request);
            cartService.addToCart(skuId,userTempId,skuNum);
        }
        cartService.addToCart(skuId,userId,skuNum);
        return Result.ok();
    }

    // 获取购物车列表
    @GetMapping("/cartList")
    public Result getCartList(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        List<CartInfo> cartList = cartService.getCartList(userId, userTempId);
        return Result.ok(cartList);
    }

    //  选中状态
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked,
                            HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if(StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.checkCart(userId,isChecked,skuId);
        return Result.ok();
    }

    // 删除购物项
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if(StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.deleteCart(skuId,userId);
        return Result.ok();
    }

    // 根据用户Id 查询已选中的购物车列表
    @GetMapping("/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable String userId){
        return cartService.getCartCheckedList(userId);
    }





}
