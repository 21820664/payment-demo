package com.hsxy.paymentdemo.task;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.RefundInfo;
import com.hsxy.paymentdemo.enums.PayType;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.RefundInfoService;
import com.hsxy.paymentdemo.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @name AliPayTask
 * @Description 支付宝定时任务
 * @author WU
 * @Date 2022/9/4 11:49
 */
@Slf4j
@Component
@Deprecated //已弃用
public class AliPayTask {
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private AliPayService aliPayService;
	/**
	 * 从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void orderConfirm() throws Exception {
		log.info("orderConfirm 被执行......");
		//未改前:
		List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(1);
		//改后:
		//List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(1,PayType.ALIPAY.getType());
		for (OrderInfo orderInfo : orderInfoList) {
			String orderNo = orderInfo.getOrderNo();
			log.warn("超时订单 ===> {}", orderNo);
			//核实订单状态：调用支付宝查单接口
			//aliPayService.checkOrderStatus(orderNo);
		}
	}
}