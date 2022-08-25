package com.hsxy.paymentdemo.service;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderInfoService extends IService<OrderInfo> {
	
	/**
	 * @Description 通过商品ID创建订单
	 * @Param [productId]
	 * @return com.hsxy.paymentdemo.entity.OrderInfo
	 */
	OrderInfo createOrderByProductId(Long productId);
	
	/**
	 * @Description 缓存二维码
	 * @Param [orderNo, codeUrl]
	 * @return void
	 */
	void saveCodeUrl(String orderNo, String codeUrl);
}
