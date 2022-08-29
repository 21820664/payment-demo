package com.hsxy.paymentdemo.enums.wxpay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信API类型
 * 写这个枚举类也是因为懒，没其他的原因，里面的内容就是微信支付对应功能的API，这些在微信官网全部都能找到。前面那串是固定的API地址，我们在配置文件里面写死的那个domain字段就是，所以我们把后面那串内容也弄成枚举类的话，我们使用的时候就可以直接把这两个东西拼接起来了，就不用每次都敲一遍
 */
@AllArgsConstructor
@Getter
public enum WxApiType {

	/**
	 * Native下单
	 */
	NATIVE_PAY("/v3/pay/transactions/native"),

	/**
	 * 查询订单
	 */
	ORDER_QUERY_BY_NO("/v3/pay/transactions/out-trade-no/%s"),

	/**
	 * 关闭订单
	 */
	CLOSE_ORDER_BY_NO("/v3/pay/transactions/out-trade-no/%s/close"),

	/**
	 * 申请退款
	 */
	DOMESTIC_REFUNDS("/v3/refund/domestic/refunds"),

	/**
	 * 查询单笔退款
	 */
	DOMESTIC_REFUNDS_QUERY("/v3/refund/domestic/refunds/%s"),

	/**
	 * 申请交易账单
	 */
	TRADE_BILLS("/v3/bill/tradebill"),

	/**
	 * 申请资金账单
	 */
	FUND_FLOW_BILLS("/v3/bill/fundflowbill"),
	
	/**
	 * Native下单V2
	 */
	NATIVE_PAY_V2("/pay/unifiedorder");
	
	
	/**
	 * 类型
	 */
	private final String type;
}
