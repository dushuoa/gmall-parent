package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.mapper.CommentInfoRepository;
import com.atguigu.gmall.item.service.CommentInfoService;
import com.atguigu.gmall.model.comment.CommentInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:49
 * @Version 1.0
 */
@Service
public class CommentInfoServiceImpl implements CommentInfoService {

    @Autowired
    private CommentInfoRepository commentInfoRepository;


    // 保存评价信息
    @Override
    public void save(List<CommentInfo> commentInfoList) {
        commentInfoRepository.saveAll(commentInfoList);
    }

    @Override
    public IPage<CommentInfo> getPageList(Long page, Long limit, Long spuId) {
        List<CommentInfo> list = commentInfoRepository.findBySpuId(spuId);
        List<CommentInfo> result = list.stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        IPage<CommentInfo> iPage = new Page<>();
        iPage.setRecords(result);
        iPage.setTotal(list.size());
        iPage.setCurrent(page);
        // 求总页数  总记录数 / 每页数量 向上取整
        long totalPages = (long) Math.ceil(result.size () / limit);
        iPage.setPages(totalPages);
        return iPage;
    }
}
