package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.item.service.CommentInfoService;
import com.atguigu.gmall.model.comment.CommentInfo;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.service.UserFeignClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:40
 * @Version 1.0
 */
@RestController
@RequestMapping("api/comment/commentInfo")
public class CommentInfoApiController {

    @Autowired
    private CommentInfoService commentInfoService;

    @Resource
    private UserFeignClient userFeignClient;

    // api/comment/commentInfo/auth/save
    // 保存评价信息 POST
    @PostMapping("/auth/save")
    public Result savaComment(@RequestBody List<CommentInfo> commentInfoList,
                              HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if(!CollectionUtils.isEmpty(commentInfoList)){
            for (CommentInfo commentInfo : commentInfoList) {
                commentInfo.setUserId(Long.parseLong(userId));
                UserInfo userInfo = userFeignClient.getUserInfoByUserId(Long.parseLong(userId));
                commentInfo.setNickName(userInfo.getNickName());
                commentInfo.setHeadImg(userInfo.getHeadImg());
            }
            commentInfoService.save(commentInfoList);
            return Result.ok();
        }
        return Result.fail();
    }

    // 获取评论列表
    @GetMapping("/{spuId}/{page}/{limit}")
    public Result getPageList(@PathVariable Long spuId,
                              @PathVariable Long page,
                              @PathVariable Long limit){
        IPage<CommentInfo> pageResult = commentInfoService.getPageList(page,limit,spuId);
        return Result.ok(pageResult);
    }



}
