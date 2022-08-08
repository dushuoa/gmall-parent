package com.atguigu.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.IPUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author dushuo
 * @Date 2022/8/7 19:40
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    // 用户登录
    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request){
        if(userInfo!=null){
            // 根据用户输入的查询用户信息
            UserInfo info = userService.login(userInfo);
            if(info == null){
                return Result.fail().message("登录失败");
            }
            HashMap<String, Object> map = new HashMap<>();
            String token = UUID.randomUUID().toString();

            // 记录到redis，方便其他微服务查看
            String loginKey = RedisConst.USER_LOGIN_KEY_PREFIX+token;
            // 防止token被窃取，我们记录ip地址
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",info.getId().toString());
            jsonObject.put("ip", IpUtil.getIpAddress(request).toString());
            redisTemplate.opsForValue().set(loginKey,jsonObject.toJSONString(),RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);

            // 获取nickName
            String nickName = info.getNickName();

            map.put("token",token);
            map.put("nickName",nickName);
            return Result.ok(map);
        }else {
            return Result.fail().message("登录失败");
        }
    }


    // 退出登录
    @GetMapping("logout")
    public Result logout(@RequestHeader String token){
        if(!StringUtils.isEmpty(token)){
            String loginKey = RedisConst.USER_LOGIN_KEY_PREFIX+token;
            redisTemplate.delete(loginKey);
            return Result.ok();
        }
        return Result.fail();
    }





}
