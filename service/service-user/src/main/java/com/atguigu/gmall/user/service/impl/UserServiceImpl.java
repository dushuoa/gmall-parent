package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @Author dushuo
 * @Date 2022/8/7 19:34
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoMapper userInfoMapper;

    // 用户登录
    @Override
    public UserInfo login(UserInfo userInfo) {
        if(userInfo != null){
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("login_name",userInfo.getLoginName());
            String passwd = userInfo.getPasswd();
            passwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
            wrapper.eq("passwd",passwd);
            UserInfo userInfo1 = userInfoMapper.selectOne(wrapper);
            if(userInfo1 != null){
                return userInfo1;
            }
        }
        return null;
    }




}
