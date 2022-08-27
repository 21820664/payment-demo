package com.hsxy.paymentdemo.service.impl;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.RefundInfo;
import com.hsxy.paymentdemo.mapper.RefundInfoMapper;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.RefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hsxy.paymentdemo.util.OrderNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
	
	@Resource
	private OrderInfoService orderInfoService;

	@Override
	public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
		//根据订单号获取订单信息
		OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
		//根据订单号生成退款订单
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setOrderNo(orderNo);//订单编号
		refundInfo.setRefundNo(OrderNoUtils.getRefundNo());//退款单编号
		refundInfo.setTotalFee(orderInfo.getTotalFee());//原订单金额(分)
		refundInfo.setRefund(orderInfo.getTotalFee());//退款金额(分)
		refundInfo.setReason(reason);//退款原因
		//保存退款订单
		baseMapper.insert(refundInfo);
		return refundInfo;
	}
}
