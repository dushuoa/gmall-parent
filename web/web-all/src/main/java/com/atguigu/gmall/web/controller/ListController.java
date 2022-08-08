package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/6 19:13
 * @Version 1.0
 *
 * 全文检索
 */
@Controller
public class ListController {

    @Resource
    private ListFeignClient listFeignClient;

    /**
     * 列表搜索
     * @param searchParam 检索参数
     * @return 查询结果
     */
    @GetMapping("list.html")
    public String search(SearchParam searchParam, Model model){
        Result<Map> result = listFeignClient.search(searchParam);
        String urlParam = makeUrlParam(searchParam);

        // 制作品牌面包屑
        String trademarkParam = makeTradeMarkParam(searchParam.getTrademark());

        // 制作平台属性的面包屑
        List<Map> propsParamList = makePropsParam(searchParam.getProps());

        // 排序
        Map<String, Object> orderMap = makeOrderMap(searchParam.getOrder());

        model.addAllAttributes(result.getData());
        model.addAttribute("urlParam",urlParam);
        model.addAttribute("goodsList",result.getData().get("goodsList"));
        model.addAttribute("trademarkParam",trademarkParam);
        model.addAttribute("propsParamList",propsParamList);
        model.addAttribute("orderMap",orderMap);
        return "list/index";
    }

    // 排序 2:desc
    private Map<String, Object> makeOrderMap(String order) {
        HashMap<String, Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(order)){
            String[] split = order.split(":");
            if(split.length == 2){
                map.put("type",split[0].toString());
                map.put("sort",split[1].toString());
            }
        }else {
            map.put("type","1");
            map.put("sort","desc");
        }
        return map;
    }

    // 制作平台属性的面包屑
    private List<Map> makePropsParam(String[] props) {
        List<Map> list = new ArrayList<>();
        if(props != null){
            for (String prop : props) {
                // prop = 23:8G:运行内存
                String[] split = prop.split(":");
                if(split.length == 3){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("attrName",split[2]);
                    map.put("attrValue",split[1]);
                    map.put("attrId",split[0]);
                    list.add(map);
                }
            }
        }
        return list;
    }

    // 品牌面包屑 1:小米
    private String makeTradeMarkParam(String trademark) {
        if(!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            if(split.length == 2){
                return "品牌: "+split[1].toString();
            }
        }
        return null;
    }

    // 拼接请求参数的url
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        // 判断是否是通过关键字查找
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        // 判断一二三级分类id
        if(!StringUtils.isEmpty(searchParam.getCategory1Id())){
            urlParam.append("category1Id=").append(searchParam.getCategory1Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory2Id())){
            urlParam.append("category2Id=").append(searchParam.getCategory2Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory3Id())){
            urlParam.append("category3Id=").append(searchParam.getCategory3Id());
        }
        // 拼接品牌
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            if(urlParam.length()>0){
                urlParam.append("&trademark=").append(searchParam.getTrademark());
            }
        }
        // 拼接平台属性
        String[] props = searchParam.getProps();
        if(props != null){
            if(props.length > 0){
                for (String prop : props) {
                    if(urlParam.length()>0){
                        // &props=23:8G:运行内存&props=24:128G:
                        urlParam.append("&props=").append(prop);
                    }
                }
            }
        }
        return "list.html?"+ urlParam.toString();
    }



}
