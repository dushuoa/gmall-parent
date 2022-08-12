package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author dushuo
 * @Date 2022/8/10 18:38
 * @Version 1.0
 */
@Controller
public class OrderController {

    @Resource
    private OrderFeignClient orderFeignClient;


    @GetMapping("trade.html")
    public String trade(Model model){
        Result<Map> trade = orderFeignClient.trade();
        model.addAllAttributes(trade.getData());
        return "order/trade";
    }

    @GetMapping("myOrder.html")
    public String myOrder(){
        return "order/myOrder";
    }


    @GetMapping("comment.html")
    public String comment(Model model, HttpServletRequest request){
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderFeignClient.getCommentInfo(Long.parseLong(orderId));
        model.addAttribute("orderInfo",orderInfo);
        return "comment/index";
    }


}
