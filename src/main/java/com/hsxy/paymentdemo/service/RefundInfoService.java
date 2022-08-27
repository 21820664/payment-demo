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
}
