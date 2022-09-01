package com.hsxy.paymentdemo.service;

import java.util.HashMap;
import java.util.Map;

public interface PaymentInfoService {
	
	/**
	 * @Description 记录支付日志(微信版)
	 * @Param [plainText] 解密明文
	 * @return void
	 */
	void createPaymentInfo(String plainText);
	
	/**
	 * @Description 记录支付日志(支付宝版)
	 * @Param [params] 返回参数
	 * @return void
	 */
	void createPaymentInfoForAlipay(Map<String, String> params);
}
