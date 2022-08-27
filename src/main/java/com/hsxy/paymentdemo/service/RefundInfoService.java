package com.hsxy.paymentdemo.service;

import com.hsxy.paymentdemo.entity.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description 退款订单
 */
public interface RefundInfoService extends IService<RefundInfo> {
	
	/**
	 * @Description 创建退款单记录
	 * @Param [orderNo, reason] 订单号,退款原因
	 * @return com.hsxy.paymentdemo.entity.RefundInfo
	 */
	RefundInfo createRefundByOrderNo(String orderNo, String reason);
	
	/**
	 * @Description 更新退款单
	 * @Param [bodyAsString] 微信支付返回通知请求体
	 * @return void
	 */
	void updateRefund(String bodyAsString);
}
