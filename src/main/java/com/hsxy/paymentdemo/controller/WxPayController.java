package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hsxy.paymentdemo.config.WxPayConfig;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.util.HttpUtils;
import com.hsxy.paymentdemo.vo.AjaxResult;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	
	@Resource
	private WxPayConfig wxPayConfig;
	
	/**
	 * @Description 在WxPayConfig中配置了获取签名验证器:Verifier getVerifier()
	 */
	@Resource
	private Verifier verifier;
	
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
			
			String apiV3Key = wxPayConfig.getApiV3Key();
			//从微信支付回调请求头获取必要信息
			String wechatPaySerial = request.getHeader("Wechatpay-Serial");//应答平台证书序列号
			String nonce = request.getHeader("Wechatpay-Nonce");//应答随机串
			//String nonce = (String) bodyMap.get("nonce");//应答随机串(!不一致,需从请求头获取)
			String timestamp = request.getHeader("Wechatpay-Timestamp");//时间戳
			String signature = request.getHeader("Wechatpay-Signature");//签名
			
			//处理通知参数
			String body = HttpUtils.readData(request);//应答主体 [readData(request)同一request只能使用一次 ∵ br.close()]
			Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
			String notifyId = (String) bodyMap.get("id");//支付通知的id
			
			log.info("支付通知的id ===> {}", notifyId);
			log.info("支付通知的完整数据 ===> {}", body);
			log.info("_________________________");
			log.info("平台证书序列号 ===> {}", wechatPaySerial);
			log.info("nonce ===> {}", nonce);
			log.info("timestamp ===> {}", timestamp);
			log.info("signature ===> {}", signature);
			//DO : 签名的验证
			// 构建request，传入必要参数
			NotificationRequest notificationRequest = new NotificationRequest.Builder()
					.withSerialNumber(wechatPaySerial)
					.withNonce(nonce)
					.withTimestamp(timestamp)
					.withSignature(signature)
					.withBody(body)
					.build();
			NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));
			//JSON.parseObject，是将Json字符串转化为相应的对象；JSON.toJSONString则是将对象转化为Json字符串.用 Gson.toJson也行
			// 验签和解析请求体
			Notification notification = handler.parse(notificationRequest);
			/*Notification notification = wechatPay2ValidatorForRequest.notificationHandler();//旧版需要自己写*/
			
			// 从notification中获取解密报文。
			String plainText = notification.getDecryptData();
			log.info("解密报文---> {}" , plainText);
			
			String eventType = notification.getEventType();
			if(eventType.length() == 0){
				log.error("支付回调通知验签失败");
				response.setStatus(500);
				map.put("code","ERROR");
				map.put("message","失败");
				return gson.toJson(map);
			}
			log.info("支付回调通知验签成功");
			
			//DO : 处理订单:通过解密报文
			wxPayService.processOrder(plainText);
			
			//应答超时
			//设置响应超时，可以接收到微信支付的重复的支付结果通知。
			//通知重复，数据库会记录多余的支付日志
			//TimeUnit.SECONDS.sleep(5);
			
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