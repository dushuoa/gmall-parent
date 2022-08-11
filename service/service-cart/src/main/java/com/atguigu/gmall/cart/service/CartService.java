package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/9 18:13
 * @Version 1.0
 */
public interface CartService {

    /**
     * 添加购物车
     * @param skuId 商品Id
     * @param userId 用户Id
     * @param skuNum 商品数量
     */
    void addToCart(Long skuId,String userId,Integer skuNum);

    /**
     * 通过用户Id 查询购物车列表
     * @param userId 用户Id
     * @param userTempId 用户临时id controller获取
     * @return 购物车列表
     */
    List<CartInfo> getCartList(String userId, String userTempId);


    /**
     * 更新选中状态
     * @param userId 用户Id
     * @param isChecked 选中状态
     * @param skuId 商品id
     */
    void checkCart(String userId, Integer isChecked, Long skuId);

    /**
     * 删除购物项
     * @param skuId 商品id
     * @param userId 用户Id
     */
    void deleteCart(Long skuId, String userId);

    /**
     * 根据用户Id 查询已选中的购物车列表
     * @param userId 用户Id
     * @return 购物车列表
     */
    List<CartInfo> getCartCheckedList(String userId);



}
