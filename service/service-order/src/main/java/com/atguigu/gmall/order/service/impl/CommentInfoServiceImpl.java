package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.comment.CommentInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.CommentInfoRepository;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.CommentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:49
 * @Version 1.0
 */
@Service
public class CommentInfoServiceImpl implements CommentInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CommentInfoRepository commentInfoRepository;

    // 根据订单id获取订单的详细数据
    @Override
    public OrderInfo getCommentInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orderId));
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    // 保存评价信息
    @Override
    public void save(List<CommentInfo> commentInfoList) {
        commentInfoRepository.saveAll(commentInfoList);
    }
}
