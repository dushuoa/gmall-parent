package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:39
 * @Version 1.0
 */
public interface UserAddressService {

    /**
     * 根据用户Id 查询用户的收货地址列表！
     * @param userId 用户Id
     * @return 收货地址列表
     */
    List<UserAddress> findUserAddressListByUserId(String userId);

}
