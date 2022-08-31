package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @name AliPayController
 * @Description 控制器:支付宝
 * @author WU
 * @Date 2022/8/31 17:44
 */
@CrossOrigin //开放前端的跨域访问
@RestController
@RequestMapping("/api/ali-pay")
@Api(tags = "网站支付宝支付")
@Slf4j
public class AliPayController {
	@Resource
	private AliPayService aliPayService;
	
	@ApiOperation("统一收单下单并支付页面接口的调用")
	@PostMapping("/trade/page/pay/{productId}")
	public AjaxResult tradePagePay(@PathVariable Long productId){
		log.info("统一收单下单并支付页面接口的调用");
		//支付宝开放平台接受 request 请求对象后
		// 会为开发者生成一个html 形式的 form表单，包含自动提交的脚本
		String formStr = aliPayService.tradeCreate(productId);
		//我们将form表单字符串返回给前端程序，之后前端将会调用自动提交脚本，进行表单的提交
		//此时，表单会自动提交到action属性所指向的支付宝开放平台中，从而为用户展示一个支付页面
		return AjaxResult.ok().data("formStr", formStr);
	}
}