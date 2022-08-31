package com.hsxy.paymentdemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @name AliPayServiceImpl
 * @Description
 * @author WU
 * @Date 2022/8/31 17:51
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
	
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private AlipayClient alipayClient;
	@Resource
	private Environment config;
	@Transactional(rollbackFor = Exception.class)//需显示指定rollback
	@Override
	public String tradeCreate(Long productId) {
		try {
			//1.生成订单
			log.info("生成订单");
			OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

			//调用支付宝接口
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			//配置需要的公共请求参数
			//request.setNotifyUrl("");
			//支付完成后，我们想让页面跳转回支付成功的页面，配置returnUrl
			request.setReturnUrl(config.getProperty("alipay.return-url"));

				//组装当前业务方法的请求参数
				JSONObject bizContent = new JSONObject();
				bizContent.put("out_trade_no", orderInfo.getOrderNo());
				//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
				//先转成String再转成BigDecimal
				BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
				bizContent.put("total_amount", total);//订单总金额
				bizContent.put("subject", orderInfo.getTitle());//订单标题
				bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//销售产品码
			//将bizContent从JSONObject --> JSON(String)
			request.setBizContent(bizContent.toString());

			//执行请求，调用支付宝接口
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			if(response.isSuccess()){
				log.info("调用成功，返回结果 ===> " + response.getBody());
				return response.getBody();
			} else {
				log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
				throw new RuntimeException("创建支付交易失败");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new RuntimeException("创建支付交易失败");
		}
	}

}
