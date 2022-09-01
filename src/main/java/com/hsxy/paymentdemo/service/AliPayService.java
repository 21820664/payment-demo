package com.hsxy.paymentdemo.service;

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
}
