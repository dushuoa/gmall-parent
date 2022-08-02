package com.atguigu.gmall.common.cache;

import java.lang.annotation.*;

/**
 * @Author dushuo
 * @Date 2022/8/2 16:09
 * @Version 1.0
 */
@Target(ElementType.METHOD) // 定义注解可以用在方法上
@Retention(RetentionPolicy.RUNTIME) // 定义注解的生效范围
@Documented
public @interface GmallCache {

    // 定义一个前缀，设置默认值
    String prefix() default "cache:";

}
