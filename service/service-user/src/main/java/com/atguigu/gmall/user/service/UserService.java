package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

/**
 * @Author dushuo
 * @Date 2022/8/7 19:33
 * @Version 1.0
 */
public interface UserService {

    /**
     * 用户登录
     * @param userInfo 参数
     * @return 查询出的记录
     */
    UserInfo login(UserInfo userInfo);

}
