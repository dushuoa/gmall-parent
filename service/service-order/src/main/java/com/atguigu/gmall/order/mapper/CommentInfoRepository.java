package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.comment.CommentInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author dushuo
 * @Date 2022/8/12 19:55
 * @Version 1.0
 */
@Repository
public interface CommentInfoRepository extends MongoRepository<CommentInfo,Long> {
}
