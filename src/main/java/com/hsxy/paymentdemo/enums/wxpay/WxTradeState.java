package com.hsxy.paymentdemo.enums.wxpay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信订单状态:发生在商户和微信平台
 */
@AllArgsConstructor
@Getter
public enum WxTradeState {

    /**
     * 支付成功
     */
    SUCCESS("SUCCESS"),

    /**
     * 未支付
     */
    NOTPAY("NOTPAY"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED"),

    /**
     * 转入退款
     */
    REFUND("REFUND");

    /**
     * 类型
     */
    private final String type;
}
