package com.atguigu.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AliPayService;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/payment/alipay")
public class AlipayController {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${app_id}")
    private String app_id;

    // 生成支付二维码
    @RequestMapping("submit/{orderId}")
    @ResponseBody
    public String submitOrder(@PathVariable Long orderId){
        String from = aliPayService.createaliPay(orderId);
        return from;
    }


    // 支付成功，同步回调
    @RequestMapping("/callback/return")
    public String callBack(){
        // http://payment.gmall.com/pay/success.html
        return "redirect:"+ AlipayConfig.return_order_url;
    }

    // 异步回调
    @PostMapping("/callback/notify")
    @ResponseBody
    public String callbackNotify(@RequestParam Map<String,String> paramsMap){
        System.out.println("你回来了.....");
        // Map<String, String> paramsMap = ... // 将异步通知中收到的所有参数都存放到map中
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //  获取异步通知的参数中的订单号！
        String outTradeNo = paramsMap.get("out_trade_no");
        //  获取异步通知的参数中的订单总金额！
        String totalAmount = paramsMap.get("total_amount");
        //  获取异步通知的参数中的appId！
        String appId = paramsMap.get("app_id");
        //  获取异步通知的参数中的交易状态！
        String tradeStatus = paramsMap.get("trade_status");
        //  根据outTradeNo 和 支付方式 查询数据！
        PaymentInfo paymentinfo = this.paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
        //  保证异步通知的幂等性！notify_id
        String notifyId = paramsMap.get("notify_id");

        //  true:
        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            if (paymentinfo==null || new BigDecimal("0.01").compareTo(new BigDecimal(totalAmount))!=0
                    || !appId.equals(app_id)){
                return "failure";
            }
            //  放入redis！ setnx：当 key 不存在的时候生效！
            Boolean flag = this.redisTemplate.opsForValue().setIfAbsent(notifyId, notifyId, 10, TimeUnit.MINUTES);
            //  说明已经处理过了！
            if (!flag){
                return "failure";
            }
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)){
                //  修改交易记录状态！再订单状态！
                this.paymentService.paySuccess(outTradeNo, PaymentType.ALIPAY.name(),paramsMap);
                //  this.paymentService.paySuccess(paymentinfo.getId(),paramsMap);
                return "success";
            }
        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }

    @RequestMapping("refund/{orderId}")
    @ResponseBody
    public Result refund(@PathVariable(value = "orderId")Long orderId) {
        // 调用退款接口
        boolean flag = aliPayService.refund(orderId);

        return Result.ok(flag);
    }

    // 根据订单Id关闭订单
    @GetMapping("closePay/{orderId}")
    @ResponseBody
    public Boolean closePay(@PathVariable Long orderId){
        Boolean aBoolean = aliPayService.closePay(orderId);
        return aBoolean;
    }

    // 查看是否有交易记录
    @RequestMapping("checkPayment/{orderId}")
    @ResponseBody
    public Boolean checkPayment(@PathVariable Long orderId){
        // 调用退款接口
        boolean flag = aliPayService.checkPayment(orderId);
        return flag;
    }

    @GetMapping("getPaymentInfo/{outTradeNo}")
    @ResponseBody
    public PaymentInfo getPaymentInfo(@PathVariable String outTradeNo){
        PaymentInfo paymentInfo = paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
        if (null!=paymentInfo){
            return paymentInfo;
        }
        return null;
    }




}
