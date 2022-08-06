package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Author dushuo
 * @Date 2022/8/3 21:25
 * @Version 1.0
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private SearchService searchService;

    @GetMapping("/createIndex")
    public Result createIndex(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 上架商品
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新商品incrHotScore
     * @param skuId 商品id
     * @return ok
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    /**
     * 根据搜索参数查询结果列表
     * @param searchParam 搜索参数
     * @return 结果
     */
    @PostMapping
    public Result search(@RequestBody SearchParam searchParam){
        try {
            SearchResponseVo searchResponseVo = searchService.search(searchParam);
            return Result.ok(searchResponseVo);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





}
