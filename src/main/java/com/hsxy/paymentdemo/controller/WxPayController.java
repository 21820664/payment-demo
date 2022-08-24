package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @name WxPayController
 * @Description 控制器:网站微信支付
 * @author WU
 * @Date 2022/8/24 15:43
 */
@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "网站微信支付 WxPay")
@Slf4j
public class WxPayController {
	@Resource
	private WxPayService wxPayService;
	
	@ApiOperation("调用统一下单API，生成支付二维码")
	@PostMapping("/native/{productId}")
	public AjaxResult nativePay(@PathVariable Long productId) throws Exception {
		log.info("------发起支付请求------");
		//返回支付二维码连接和订单号
		Map<String, Object> map = wxPayService.nativePay(productId);
		
		//因为AjaxResult的set返回为void,无法进行链式操作(不美观),
		//在AjaxResult上加上注解@Accessors(chain = true)解决
		/*AjaxResult ok = AjaxResult.ok();
		ok.setData(map);*/
		return AjaxResult.ok().setData(map);
	}
}