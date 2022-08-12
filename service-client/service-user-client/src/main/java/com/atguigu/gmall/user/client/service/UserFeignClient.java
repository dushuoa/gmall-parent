package com.atguigu.gmall.user.client.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.service.impl.UserFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author dushuo
 * @Date 2022/8/10 16:47
 * @Version 1.0
 */
@FeignClient(value = "service-user",fallback = UserFeignClientImpl.class)
public interface UserFeignClient {

    @GetMapping("/api/user/inner/findUserAddressListByUserId/{userId}")
    public Result findUserAddressListByUserId(@PathVariable String userId);

    @GetMapping("/api/user/inner/{userId}")
    public UserInfo getUserInfoByUserId(@PathVariable Long userId);

}
