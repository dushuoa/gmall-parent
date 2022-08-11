package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author dushuo
 * @Date 2022/8/10 19:09
 * @Version 1.0
 */
@Mapper
public interface OrderServiceMapper extends BaseMapper<OrderInfo> {

}
