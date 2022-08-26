package com.hsxy.paymentdemo.service;

import java.util.HashMap;
import java.util.Map;

public interface PaymentInfoService {
	
	/**
	 * @Description 记录支付日志
	 * @Param [plainText]
	 * @return void
	 */
	void createPaymentInfo(String plainText);
}
