package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author dushuo
 * @Date 2022/8/3 19:23
 * @Version 1.0
 */
@Configuration
public class ThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                8,
                20,
                3,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
    }


}
