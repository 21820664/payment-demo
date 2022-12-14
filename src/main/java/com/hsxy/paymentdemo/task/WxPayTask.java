package com.hsxy.paymentdemo.task;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.RefundInfo;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.RefundInfoService;
import com.hsxy.paymentdemo.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @name WxPayTask
 * @Description 微信定时任务
 * @author WU
 * @Date 2022/8/27 14:34
 */
@Slf4j
@Component
@Deprecated //已弃用
public class WxPayTask {
	/**
	 * 测试
	 * (cron="秒 分 时 日 月 周")
	 * *：每隔一秒执行
	 * 0/3：从第0秒开始，每隔3秒执行一次
	 * 1-3: 从第1秒开始执行，到第3秒结束执行
	 * 1,2,3：第1、2、3秒执行
	 * ?：不指定，若指定日期，则不指定周，反之同理
	 */
	/*@Scheduled(cron="0/3 * * * * ?")
	public void task1() {
		log.info("task1 执行");
	}*/
	
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private WxPayService wxPayService;

	/**
	 * @Description 订单确认:从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
	 * @Param []
	 * @return void
	 */
	//@Scheduled(cron = "0/30 * * * * ?")
	public void orderConfirm() throws Exception {
		log.info("orderConfirm 定时被执行......");
		//找出创建超过5分钟，并且未支付的订单
		List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(5);
		for (OrderInfo orderInfo : orderInfoList) {
			String orderNo = orderInfo.getOrderNo();
			log.warn("超时订单 ===> {}", orderNo);
			//核实订单状态：调用微信支付查单接口
			wxPayService.checkOrderStatus(orderNo);
		}
	}
	
	@Resource
	private RefundInfoService refundInfoService;

	/**
	 * @Description 从第0秒开始每隔30秒执行1次，查询创建退款超过5分钟，并且未成功的退款单
	 * @Param []
	 * @return void
	 */
	//@Scheduled(cron = "0/30 * * * * ?")
	public void refundConfirm() throws Exception {
		log.info("refundConfirm 定时被执行......");
		//找出申请退款超过5分钟并且未成功的退款单
		List<RefundInfo> refundInfoList = refundInfoService.getNoRefundOrderByDuration(5);
		for (RefundInfo refundInfo : refundInfoList) {
			String refundNo = refundInfo.getRefundNo();
			log.warn("超时未退款的退款单号 ===> {}", refundNo);
			//核实订单状态：调用微信支付查询退款接口
			wxPayService.checkRefundStatus(refundNo);
		}
	}
}
