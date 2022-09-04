package com.hsxy.paymentdemo.enums.alipay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @name AliTradeState
 * @Description 支付宝订单状态:发生在商户和支付宝平台
 * @author WU
 * @Date 2022/9/4 12:26
 */
@AllArgsConstructor
@Getter
public enum AliTradeState {
	
	//交易创建，等待买家付款(未支付)
	WAIT_BUYER_PAY("WAIT_BUYER_PAY"),
	
	//未付款交易超时关闭，或支付完成后全额退款
	TRADE_CLOSED("TRADE_CLOSED"),
	
	//交易支付成功
	TRADE_SUCCESS("TRADE_SUCCESS"),
	
	//交易结束，不可退款(默认支付成功一年后)
	TRADE_FINISHED("TRADE_FINISHED");
	
	/**
	 * 类型
	 */
	private final String type;
}
