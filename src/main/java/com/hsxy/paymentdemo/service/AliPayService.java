package com.hsxy.paymentdemo.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @name AliPayService
 * @Description
 * @author WU
 * @Date 2022/8/31 17:51
 */
public interface AliPayService {
	
	/**
	 * @Description 创建交易
	 * @Param [productId] 商品ID
	 * @return java.lang.String
	 */
	String tradeCreate(Long productId);
	
	/**
	 * @Description 处理订单
	 * @Param [params] 支付宝返回参数
	 * @return void
	 */
	void processOrder(Map<String, String> params);
	
	/**
	 * @Description 用户取消订单
	 * @Param [orderNo] 订单号
	 * @return void
	 */
	void cancelOrder(String orderNo) throws Exception;
	
	/**
	 * @Description 通过支付宝端查询订单
	 * @Param [orderNo] 订单号
	 * @return java.lang.String 返回订单查询结果，如果返回null则表示支付宝端尚未创建订单
	 */
	String queryOrder(String orderNo) throws AlipayApiException;
}
