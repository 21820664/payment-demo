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
	
	/**
	 * @Description 申请退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return void
	 */
	void refund(String orderNo, String reason) throws IOException;
	
	/**
	 * @Description 查询退款接口调用
	 * @Param [refundNo]
	 * @return java.lang.String
	 */
	String queryRefund(String refundNo) throws IOException;
	
	/**
	 * @Description 查询退款状态
	 * @Param [refundNo] 退款编号
	 * @return void
	 */
	void checkRefundStatus(String refundNo) throws IOException;
	
	/**
	 * @Description 处理退款单
	 * @Param [plainText] 解密后明文
	 * @return void
	 */
	void processRefund(String plainText);
	
	/**
	 * @Description 查询账单
	 * @Param [billDate] 订单日期(无法查询当日账单) 格式:yyyy-MM-dd 5.6日的账单记录的时间为05-06 9:00到05-07 9:00,并且在05-07 9:00后才能查到.
	 * @Param [type] 查询账单类型 选择:{交易账单 tradebill,资金账单 fundflowbill}
	 * @return java.lang.String
	 */
	String queryBill(String billDate, String type) throws IOException;
	
	/**
	 * @Description 下载账单
	 * @Param [billDate] 订单日期(无法查询当日账单) 格式:2022-08-29
	 * @Param [type] 查询账单类型 选择:{交易账单 tradebill,资金账单 fundflowbill}
	 * @return java.lang.String
	 */
	String downloadBill(String billDate, String type) throws IOException;
	
	/**
	 * @Description 创建订单，调用Native支付接口V2
	 * @Param [productId, remoteAddr] 商品ID, 远程客户端主机主机地址
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 */
	Map<String, Object> nativePayV2(Long productId, String remoteAddr) throws Exception;
}
