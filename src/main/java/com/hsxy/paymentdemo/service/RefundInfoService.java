package com.hsxy.paymentdemo.service;

import com.hsxy.paymentdemo.entity.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description 退款订单
 */
public interface RefundInfoService extends IService<RefundInfo> {
	
	/**
	 * @Description 创建退款单记录(微信/支付宝)
	 * @Param [orderNo, reason] 订单号,退款原因
	 * @return com.hsxy.paymentdemo.entity.RefundInfo
	 */
	RefundInfo createRefundByOrderNo(String orderNo, String reason);
	
	/**
	 * @Description 更新退款单(微信)
	 * @Param [bodyAsString] 微信支付返回通知请求体
	 * @return void
	 */
	void updateRefundForWxpay(String bodyAsString);
	
	/**
	 * @Description 更新退款单(支付宝)
	 * @Param [refundNo, content, refundStatus] 退订号, 支付宝支付返回通知请求体, 退订状态()
	 * @return void
	 */
	void updateRefundForAliPay(String refundNo, String content, String refundStatus);
	
	/**
	 * @Description 查询创建退款超过5分钟，并且未成功的退款单
	 * @Param [minute] 时间(分)
	 * @return java.util.List<com.hsxy.paymentdemo.entity.RefundInfo>
	 */
	List<RefundInfo> getNoRefundOrderByDuration(int minutes);
	
}
