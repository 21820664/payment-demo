package com.hsxy.paymentdemo.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

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
	
	@Resource
	private Environment config;
	@Resource
	private OrderInfoService orderInfoService;
	
	/**
	 * @Description 支付通知
	 * @Param [params] 支付宝返回参数(与微信不同,支付宝直接返回Map[更方便],而微信则返回JSON)
	 * @return java.lang.String
	 */
	@ApiOperation("支付通知")
	@PostMapping("/trade/notify")//需和回调后缀一致
	public String tradeNotify(@RequestParam Map<String, String> params){
		log.info("支付通知正在执行");
		log.info("通知参数 ===> {}", params);
		String result = "failure";
		try {//rsaCheckV1异常处理
			//异步通知验签
			boolean signVerified = AlipaySignature.rsaCheckV1(
					params,
					config.getProperty("alipay.alipay-public-key"),
					AlipayConstants.CHARSET_UTF8,
					AlipayConstants.SIGN_TYPE_RSA2); //调用SDK验证签名
			if(!signVerified){
			//验签失败则记录异常日志，并在response中返回failure.
				log.error("支付成功异步通知验签失败！");
				return result;
			}
			// 验签成功后
			log.info("支付成功异步通知验签成功！");
			
			//一.按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验(支付宝以明文形式返回支付通知<可设置为密文>)
			//1 商户需要验证该通知数据中的 out_trade_no 是否为商户系统中创建的订单号
			String outTradeNo = params.get("out_trade_no");
			OrderInfo order = orderInfoService.getOrderByOrderNo(outTradeNo);
			if(order == null){
				log.error("订单不存在");
				return result;
			}
			//2 判断 total_amount 是否确实为该订单的实际金额（即商户订单创建时的金额）
			String totalAmount = params.get("total_amount");
			//String先转成BigDecimal 改为分(数据库单位为分) 再转成int
			int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();//返回金额
			/**BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));*/
			int totalFeeInt = order.getTotalFee();//订单金额
			if(totalAmountInt != totalFeeInt){
				log.error("金额校验失败");
				return result;
			}
			//3 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方
			String sellerId = params.get("seller_id");//返回商户id
			String sellerIdProperty = config.getProperty("alipay.seller-id");//商户id
			if(!sellerId.equals(sellerIdProperty)){
				log.error("商家pid校验失败");
				return result;
			}
			//4 验证 app_id 是否为该商户本身
			String appId = params.get("app_id");//返回app-id
			String appIdProperty = config.getProperty("alipay.app-id");//app-id
			if(!appId.equals(appIdProperty)){
				log.error("appid校验失败");
				return result;
			}
			//在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS时，(支持退款时)
			// 支付宝才会认定为买家付款成功。
			//!响应参数与微信不同(trade_state)
			String tradeStatus = params.get("trade_status");//返回交易状态
			if(!"TRADE_SUCCESS".equals(tradeStatus)){
				log.error("支付未成功");
				return result;
			}
			
			//二.处理业务 修改订单状态 记录支付日志
			aliPayService.processOrder(params);
			//校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
			result = "success";
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@ApiOperation("用户取消订单")
	@PostMapping("/trade/close/{orderNo}")
	public AjaxResult cancel(@PathVariable String orderNo) throws Exception {
		log.info("取消订单");
		aliPayService.cancelOrder(orderNo);
		return AjaxResult.ok().setMessage("订单已取消");
	}
	
	@ApiOperation("查询订单：测试订单状态用")
	@GetMapping("/trade/query/{orderNo}")
	public AjaxResult queryOrder(@PathVariable String orderNo) throws AlipayApiException {
		log.info("查询订单");
		//将订单信息保存为字符串
		String bodyAsString = aliPayService.queryOrder(orderNo);
		return AjaxResult.ok().setMessage("查询成功").data("bodyAsString", bodyAsString);
	}
	
	/**
	 * @Description 申请退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("申请退款")
	@PostMapping("/trade/refund/{orderNo}/{reason}")
	public AjaxResult refunds(@PathVariable String orderNo, @PathVariable String reason) throws AlipayApiException {
		log.info("申请退款");
		aliPayService.refund(orderNo, reason);
		return AjaxResult.ok();
	}
	
	/**
	 * @Description 查询退款：测试用
	 * @Param [orderNo] 订单号 !非退款单号
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("查询退款：测试用")
	@GetMapping("/trade/fastpay/refund/{orderNo}")
	public AjaxResult queryRefund(@PathVariable String orderNo) throws Exception {
		log.info("查询退款");
		String result = aliPayService.queryRefund(orderNo);
		if (result != null){
			return AjaxResult.ok().setMessage("查询成功").data("result", result);
		}else {
			return AjaxResult.queryFailed().setMessage("查询退款失败:订单不存在");
		}
	}
}