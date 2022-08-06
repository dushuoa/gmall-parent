package com.atguigu.gmall.list.mapper;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author dushuo
 * @Date 2022/8/3 21:43
 * @Version 1.0
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
