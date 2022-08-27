package com.hsxy.paymentdemo.service;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

/**
 * @name WxPayService
 * @Description 微信支付
 * @author WU
 * @Date 2022/8/24 15:45
 */
public interface WxPayService {
	/**
	 * @Description 创建订单，调用Native支付接口
	 * @Param [productId]
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 */
	Map<String, Object> nativePay(Long productId) throws IOException;
	
	/**
	 * @Description 处理订单
	 * @Param [bodyMap]
	 * @return void
	 */
	void processOrder(String plainText);
	
	/**
	 * @Description 用户取消订单
	 * @Param [orderNo]
	 * @return void
	 */
	void cancelOrder(String orderNo) throws Exception;
}
