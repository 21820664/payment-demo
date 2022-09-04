package com.hsxy.paymentdemo.task;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.enums.PayType;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @name PayTask
 * @Description 定时任务整合
 * @author WU
 * @Date 2022/9/4 11:53
 */
@Slf4j
@Component
public class PayTask {
	@Resource
	private OrderInfoService orderInfoService;
	
	@Resource
	private WxPayService wxPayService;
	
	@Resource
	private AliPayService aliPayService;
	
	/**
	 * @Description 订单确认:从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
	 * @Param []
	 * @return void
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void orderConfirm() throws Exception {
		log.info("orderConfirm 定时被执行......");
		//找出创建超过5分钟，并且未支付的订单
		List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(1);
		//从支付服务器方获取支付状态
		for (OrderInfo orderInfo : orderInfoList) {
			String orderNo = orderInfo.getOrderNo();
			String paymentType = orderInfo.getPaymentType();
			log.warn("超时订单 ===> {}", orderNo);
			
			if ( PayType.WXPAY.getType().equals(paymentType) ) {
				//核实订单状态：调用微信支付查单接口
				wxPayService.checkOrderStatus(orderNo);
			}
			else if ( PayType.ALIPAY.getType().equals(paymentType) ){
				//核实订单状态：调用支付宝支付查单接口
				aliPayService.checkOrderStatus(orderNo);
			}
			
		}
	}
	
}
