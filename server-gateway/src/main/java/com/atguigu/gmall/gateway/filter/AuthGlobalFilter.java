package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/7 20:40
 * @Version 1.0
 */
@Component
@RefreshScope
public class AuthGlobalFilter implements GlobalFilter {


    // 匹配路径的工具类
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${authUrls.url}")
    private String authUrls;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath(); // 获取端口号后面的路径

        // 判断path是不是内部数据接口
        if(antPathMatcher.match("/**/inner/**",path)){
            ServerHttpResponse response = exchange.getResponse();
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 判断token是否被盗用
        String userId = getUserId(request);
        if("-1".equals(userId)){
            // token被盗用
            ServerHttpResponse response = exchange.getResponse();
            return out(response,ResultCodeEnum.LOGIN_AUTH);
        }
        // 限制访问带有 /api/**/auth/**的接口 这样的要求用户必须登录
        if(antPathMatcher.match("/api/**/auth/**",path)){
            if(StringUtils.isEmpty(userId)){
                // 没有登录，重定向到登录页面
                ServerHttpResponse response = exchange.getResponse();
//                response.setStatusCode(HttpStatus.SEE_OTHER);
//                response.getHeaders()
//                        .set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
//                return response.setComplete();
                return out(response,ResultCodeEnum.LOGIN_AUTH);
            }
        }
        // 当用户访问 trade.html,myOrder.html,list.html 的时候，要求必须登录
        String[] split = authUrls.split(",");
        if(split.length > 0){
            for (String control : split) {
                if(path.contains(control) && StringUtils.isEmpty(userId)){
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders()
                            .set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
                    return response.setComplete();
                }
            }
        }

        // 将用户Id保存到请求头中，给后台使用
        if(!StringUtils.isEmpty(userId)){
            request.mutate().header("userId", userId).build();
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    private String getUserId(ServerHttpRequest request) {
        String token = "";
        List<String> list = request.getHeaders().get("token");
        if(!CollectionUtils.isEmpty(list)){
            token = list.get(0);
        }
        if(!StringUtils.isEmpty(token)){
            String loginKey = "user:login:"+token;
            String strJson = (String) redisTemplate.opsForValue().get(loginKey);
            // 转换对象
            JSONObject jsonObject = JSONObject.parseObject(strJson);
            String userId = (String) jsonObject.get("userId");
            String ip = (String) jsonObject.get("ip");

            String ipAddress = IpUtil.getGatwayIpAddress(request);
            if(!ipAddress.equals(ip)){
                return "-1";
            } else {
                return userId;
            }
        }

        return null;
    }

    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum loginAuth) {
        return null;
    }


}
