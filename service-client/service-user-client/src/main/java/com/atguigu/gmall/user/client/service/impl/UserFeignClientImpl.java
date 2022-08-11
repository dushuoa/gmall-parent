package com.atguigu.gmall.user.client.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.client.service.UserFeignClient;
import org.springframework.stereotype.Component;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:47
 * @Version 1.0
 */
@Component
public class UserFeignClientImpl implements UserFeignClient {
    @Override
    public Result findUserAddressListByUserId(String userId) {
        return null;
    }
}
