package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.util.HttpUtils;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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
	
	/**
	 * @Description 微信支付通过支付通知接口将用户支付成功消息通知给商户
	 * @Param [request, response]
	 * @return java.lang.String
	 */
	@ApiOperation("支付通知")
	@PostMapping("/native/notify")
	public String nativeNotify(HttpServletRequest request, HttpServletResponse
			response){
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();//应答对象
		try {
			
			//处理通知参数
			String body = HttpUtils.readData(request);
			Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
			log.info("支付通知的id ===> {}", bodyMap.get("id"));
			log.info("支付通知的完整数据 ===> {}", body);
			//TODO : 签名的验证
			//TODO : 处理订单
			//int i = 3 / 0;
			// 测试超时应答：添加睡眠时间使应答超时
			//TimeUnit.SECONDS.sleep(5);
			//成功应答：成功应答必须为200或204，否则就是失败应答(微信写死)
			//(新版)接收成功：HTTP应答状态码需返回200或204，无需返回应答报文。
			response.setStatus(200);
			map.put("code", "SUCCESS");
			map.put("message", "成功(可以不用返回)");
			return gson.toJson(map);
		} catch (Exception e) {
			response.setStatus(500);
			map.put("code", "ERROR");
			map.put("message", "失败(异常)");
			return gson.toJson(map);
		}
	}
}