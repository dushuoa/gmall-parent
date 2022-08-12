package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.comment.CommentInfo;
import com.atguigu.gmall.model.order.OrderInfo;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:49
 * @Version 1.0
 */
public interface CommentInfoService {

    /**
     * 根据订单id获取订单的详细数据
     * @param orderId 订单id
     * @return 订单的详细数据
     */
    OrderInfo getCommentInfo(Long orderId);

    /**
     * 保存评价信息
     * @param commentInfoList 评价信息
     */
    void save(List<CommentInfo> commentInfoList);

}
