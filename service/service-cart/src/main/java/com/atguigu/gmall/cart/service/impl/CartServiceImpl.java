package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataUnit;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author dushuo
 * @Date 2022/8/9 18:15
 * @Version 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private ProductFeignClient productFeignClient;

    // 添加购物车
    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        /**
         * 购物车有:
         *      1. 更改数量
         *      2. 更新时间
         *      3. 设置默认选中
         *  购物车没有:
         *      1. 添加购物车
         */
        String cartKey = getKey(userId);

        // hget key field
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(cartKey, skuId.toString());

        if(cartInfo != null){
            // redis有
            cartInfo.setCartPrice(productFeignClient.getSkuPriceBySkuId(skuId));
            cartInfo.setUpdateTime(new Date());
            cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
            cartInfo.setIsChecked(1);
        } else {
            // redis没有
            cartInfo = new CartInfo();
            // 获取sku基本信息
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setIsChecked(1);
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setUpdateTime(new Date());
            cartInfo.setCreateTime(new Date());
            cartInfo.setSkuPrice(productFeignClient.getSkuPriceBySkuId(skuId));
            cartInfo.setSkuNum(skuNum);
        }
        redisTemplate.opsForHash().put(cartKey,skuId.toString(),cartInfo);
    }

    // 展示购物车
    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {

        /**
         * 合并购物，三种情况
         *          case1. userTempId有，userId没有，用户未登录情况下展示购物车数据 userTempId!=null userId=null
         *          case2. 用户登录的情况，查看临时id有没有对应的购物车
         *             1. userId != null userTempId=null 用户登录，并且没有临时id，直接返回登录的购物车
         *             2. userId != null userTempId!=null 用户登录，并且有临时id，查看两个购物车，合并，删除临时
         */
        List<CartInfo> noLoginCartInfoList = new ArrayList<>();
        if(!StringUtils.isEmpty(userTempId)){
            String cartKey = getKey(userTempId);
            noLoginCartInfoList = redisTemplate.opsForHash().values(cartKey);
        }
        if(StringUtils.isEmpty(userId)){
            if(!CollectionUtils.isEmpty(noLoginCartInfoList)){
                // 排序 如果使用stream流的方式需要返回新生成的集合
                noLoginCartInfoList
                        .sort((cartInfo1,cartInfo2)-> DateUtil.truncatedCompareTo(cartInfo2.getUpdateTime(),cartInfo1.getUpdateTime(), Calendar.SECOND));
                return noLoginCartInfoList;
            }
        }

        // 登录的购物车数据
        List<CartInfo> loginCartInfoList = new ArrayList<>();

        // 代表用户登录了
        String userIdKey = "";
        String userTempIdKey = "";
        if(!StringUtils.isEmpty(userId)){
            userIdKey = getKey(userId);
            BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(userIdKey);
            // userId != null userTempId != null 用户登录，且存在临时id
            if(!CollectionUtils.isEmpty(noLoginCartInfoList)){
                userTempIdKey = getKey(userTempId);
                // 合并两个购物车的结果 合并条件: skuId = skuId  数量: num = num + noLoginNum
                String finalUserCartKey = userIdKey;
                noLoginCartInfoList.forEach(cartInfo -> {
                    // 循环遍历未登录的集合
                    // 判断已登录的用户有没有这个skuId
                    if(Boolean.TRUE.equals(boundHashOperations.hasKey(cartInfo.getSkuId().toString()))){
                        // 如果有，合并, 规则 skuId = skuId num = num+noLoginNum
                        CartInfo loginCartInfo = boundHashOperations.get(cartInfo.getSkuId().toString());
                        // 合并数量
                        loginCartInfo.setSkuNum(cartInfo.getSkuNum()+loginCartInfo.getSkuNum());
                        // 设置价格
                        loginCartInfo.setSkuPrice(productFeignClient.getSkuPriceBySkuId(cartInfo.getSkuId()));
                        // 判断选中状态
                        if(cartInfo.getIsChecked() == 1){
                            loginCartInfo.setIsChecked(1);
                        }
                        boundHashOperations.put(cartInfo.getSkuId().toString(),loginCartInfo);
                    } else {
                        // 如果已经登录的集合里没有未登录的购物项
                        // 判断未登录的是否选中，然后直接添加
                        if(cartInfo.getIsChecked() == 1){
                            cartInfo.setUserId(userId);
                            cartInfo.setCreateTime(new Date());
                            cartInfo.setUpdateTime(new Date());
                            redisTemplate.opsForHash().put(finalUserCartKey,cartInfo.getSkuId().toString(),cartInfo);
                        }
                    }
                });
                // 合并以后删除未登录的用户数据
                redisTemplate.delete(userTempIdKey);

                // 查询最新的数据进行返回
                loginCartInfoList = redisTemplate.opsForHash().values(userIdKey);

            } else {
                // userId != null userTempId = null 用户登录，不存在临时id
                // 直接根据用户id的key查询redis
                loginCartInfoList = redisTemplate.opsForHash().values(userIdKey);
            }
            // 排序返回
            if(!CollectionUtils.isEmpty(loginCartInfoList)){
                // 按照修改日期降序排序 如果使用stream流的方式需要返回新生成的集合
                loginCartInfoList
                        .sort((cartInfo1,cartInfo2)-> DateUtil.truncatedCompareTo(cartInfo2.getUpdateTime(),cartInfo1.getUpdateTime(), Calendar.SECOND));
                return loginCartInfoList;
            }
        }
        return null;
    }

    // 更新选中状态
    @Override
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        String cartKey = getKey(userId);
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(cartKey, skuId.toString());
        if(cartInfo != null){
            cartInfo.setIsChecked(isChecked);
            redisTemplate.opsForHash().put(cartKey,skuId.toString(),cartInfo);
        }
    }

    // 删除购物项
    @Override
    public void deleteCart(Long skuId, String userId) {
        String cartKey = getKey(userId);
        redisTemplate.opsForHash().delete(cartKey,skuId.toString());
    }

    // 根据用户Id 查询已选中的购物车列表
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        String cartKey = getKey(userId);
        List<CartInfo> cartInfoList = this.redisTemplate.opsForHash().values(cartKey);
        List<CartInfo> cartInfoIsCheckList = cartInfoList.stream().filter(cartInfo -> {
            cartInfo.setSkuPrice(productFeignClient.getSkuPriceBySkuId(cartInfo.getSkuId()));
            return cartInfo.getIsChecked().intValue() == 1;
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(cartInfoIsCheckList)){
            return cartInfoIsCheckList;
        }
        return null;
    }

    private String getKey(String userId) {
        //  user:userId:cart skuId value
        String key = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        return key;
    }
}
