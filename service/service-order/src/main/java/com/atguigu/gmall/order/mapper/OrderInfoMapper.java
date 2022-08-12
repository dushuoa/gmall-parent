package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author dushuo
 * @Date 2022/8/12 16:49
 * @Version 1.0
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    // 我的订单
    IPage<OrderInfo> selectMyOrderPage(Page<OrderInfo> pageParam, String userId);
}
