package com.hsxy.paymentdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.RefundInfo;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.enums.wxpay.WxRefundStatus;
import com.hsxy.paymentdemo.mapper.RefundInfoMapper;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.RefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hsxy.paymentdemo.util.OrderNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		refundInfo.setRefund(orderInfo.getTotalFee());//退款金额(分)[全额退款]
		refundInfo.setReason(reason);//退款原因
		//保存退款订单
		baseMapper.insert(refundInfo);
		return refundInfo;
	}
	
	@Override
	public void updateRefund(String content) {
		//将json字符串转换成Map
		Gson gson = new Gson();
		Map<String, String> resultMap = gson.fromJson(content, HashMap.class);
		//根据退款单编号修改退款单
		LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(RefundInfo::getRefundNo, resultMap.get("out_refund_no"));
		//设置要修改的字段
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setRefundId(resultMap.get("refund_id"));//微信支付退款单号
		//查询退款和申请退款中的返回参数
		if(resultMap.get("status") != null){
			refundInfo.setRefundStatus(resultMap.get("status"));//退款状态
			refundInfo.setContentReturn(content);//将全部响应结果存入数据库的content字段
		}
		//退款回调中的回调参数
		if(resultMap.get("refund_status") != null){
			refundInfo.setRefundStatus(resultMap.get("refund_status"));//退款状态
			refundInfo.setContentNotify(content);//将全部响应结果存入数据库的content字段
		}
		//更新退款单
		baseMapper.update(refundInfo, queryWrapper);
	}
	
	@Override
	public List<RefundInfo> getNoRefundOrderByDuration(int minutes) {
		LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(RefundInfo::getRefundStatus, WxRefundStatus.PROCESSING.getType())
					//判断退款单创建时间是否大于n分钟(定时调用查询订单API)<查找n分钟外未支付的订单>
					.apply("TIMESTAMPDIFF(MINUTE, create_time , now()) > " + minutes);
		return baseMapper.selectList(queryWrapper);
	}
}
