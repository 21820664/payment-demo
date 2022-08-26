package com.hsxy.paymentdemo.service;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hsxy.paymentdemo.enums.OrderStatus;

import java.util.List;

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
	
	/**
	 * @Description 查询订单列表，并倒序查询(倒序一般交给前端处理)
	 * @Param []
	 * @return java.util.List<com.hsxy.paymentdemo.entity.OrderInfo>
	 */
	List<OrderInfo> listOrderByCreateTimeDesc();
	
	/**
	 * @Description 更新订单状态
	 * @Param [orderNo, orderStatus] {订单号,订单状态}
	 * @return void
	 */
	void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);
}
