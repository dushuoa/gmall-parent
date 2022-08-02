package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author dushuo
 * @Date 2022/8/2 16:46
 * @Version 1.0
 */
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint pjp){
        // 定义一个返回结果对象
        Object object = new Object();
        // 获取方法签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 获取定义在方法上的注解
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
        // 拿到该注解的前缀
        String prefix = gmallCache.prefix();
        // 获取该方法的参数
        Object[] args = pjp.getArgs();
        // 拼接成skuKey
        String skuKey = prefix+ Arrays.asList(args).toString();
        // 查询redis缓存
        try {
            // 获取实际的返回值对象
            Class returnType = signature.getReturnType();
            object = getDataFromRedis(skuKey,returnType);
            if(object == null){
                // 如果缓存中没有
                // 加锁，查询数据库，防止缓存击穿
                String lockName = prefix+"lock";
                RLock lock = redissonClient.getLock(lockName);
                boolean result = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if(result){
                    try {
                        object = pjp.proceed();
                        if(object == null){
                            object = new Object();
                            redisTemplate.opsForValue().set(skuKey,JSON.toJSONString(object), RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                            return object;
                        }
                        // 如果数据库中存在, 我们并不知道要存储哪种数据类型, 所以我们转换成json串以后在存入缓存
                        String strJson = JSON.toJSONString(object);
                        redisTemplate.opsForValue().set(skuKey,strJson,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                        return object;
                    } finally {
                        // 释放锁
                        lock.unlock();
                    }
                }else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.cacheAroundAdvice(pjp);
                }
            }else {
                return object;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 如果redis宕机了，用数据库顶
        try {
            object = pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return object;
    }

    // 查询redis缓存
    private Object getDataFromRedis(String skuKey, Class returnType) {
        // 取到的json字符串
        String strJson = (String) redisTemplate.opsForValue().get(skuKey);
        // 转换成真正返回的对象
        return JSON.parseObject(strJson, returnType);
    }


}
