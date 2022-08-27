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
	
	/**
	 * @Description 通过微信端查询订单
	 * @Param [orderNo]
	 * @return java.lang.String
	 */
	String queryOrder(String orderNo) throws IOException;
	
	/**
	 * @Description 根据订单号查询微信支付查单接口，核实订单状态
	 * * 如果订单已支付，则更新商户端订单状态，并记录支付日志
	 * * 如果订单未支付，则调用关单接口关闭订单，并更新商户端订单状态
	 * @Param [orderNo] 订单号
	 * @return void
	 */
	void checkOrderStatus(String orderNo) throws Exception;
}
