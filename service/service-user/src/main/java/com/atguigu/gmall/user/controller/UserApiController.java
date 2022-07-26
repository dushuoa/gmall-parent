package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:44
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserInfoMapper userInfoMapper;


    @GetMapping("/inner/findUserAddressListByUserId/{userId}")
    public Result findUserAddressListByUserId(@PathVariable String userId){
        List<UserAddress> userAddressList = userAddressService.findUserAddressListByUserId(userId);
        return Result.ok(userAddressList);
    }

    // 获取用户详细信息
    @GetMapping("/inner/{userId}")
    public UserInfo getUserInfoByUserId(@PathVariable Long userId){
        UserInfo userInfo = userInfoMapper.selectById(userId);
        return userInfo;
    }


}
