package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author dushuo
 * @Date 2022/8/13 19:39
 * @Version 1.0
 */
@Controller
public class PaymentController {

    @Resource
    private OrderFeignClient orderFeignClient;

    @GetMapping("pay.html")
    public String payHtml(HttpServletRequest request){
        String orderId = request.getParameter("orderId");

        OrderInfo orderInfo = orderFeignClient.getCommentInfo(Long.parseLong(orderId));
        request.setAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }


}

