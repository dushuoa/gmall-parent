package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.comment.CommentInfo;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:49
 * @Version 1.0
 */
public interface CommentInfoService {


    /**
     * 保存评价信息
     * @param commentInfoList 评价信息
     */
    void save(List<CommentInfo> commentInfoList);


    /**
     * 获取评论列表
     * @param pageParam 分页参数
     * @param spuId spuId
     * @return 评论列表
     */
    IPage<CommentInfo> getPageList(Long page, Long limit, Long spuId);
}
