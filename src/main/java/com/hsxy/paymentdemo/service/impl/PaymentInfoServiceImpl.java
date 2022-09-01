package com.hsxy.paymentdemo.service.impl;

import com.google.gson.Gson;
import com.hsxy.paymentdemo.entity.PaymentInfo;
import com.hsxy.paymentdemo.enums.PayType;
import com.hsxy.paymentdemo.mapper.PaymentInfoMapper;
import com.hsxy.paymentdemo.service.PaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
	
	@Override
	public void createPaymentInfo(String plainText) {
		log.info("记录支付日志");
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
		//商户订单号
		String orderNo = (String)plainTextMap.get("out_trade_no");
		//微信支付订单号
		String transactionId = (String)plainTextMap.get("transaction_id");
		//交易类型
		String tradeType = (String)plainTextMap.get("trade_type");
		//交易状态
		String tradeState = (String)plainTextMap.get("trade_state");
		log.info("交易状态---> {}" , tradeState);
		//-订单金额
		Map<String, Object> amount = (Map)plainTextMap.get("amount");
		//坑:不能直接转为int(Gson缺陷) || 实际中应转换为BigDecimal(不要转为Double,会有精度损失[此处支付金额单位为(分)应该没事?])
		//用户实际支付金额
		//Integer payerTotal = ((BigDecimal) amount.get("payer_total")).intValue();//报错
		Integer payerTotal = ((Double) amount.get("payer_total")).intValue();
		log.info("订单实际金额---> {}" , payerTotal);
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setOrderNo(orderNo);
		paymentInfo.setPaymentType(PayType.WXPAY.getType());
		paymentInfo.setTransactionId(transactionId);
		paymentInfo.setTradeType(tradeType);
		paymentInfo.setTradeState(tradeState);
		paymentInfo.setPayerTotal(payerTotal);
		//备份解密报文(各支付厂商可能返回类型不同,以备不时之需)
		paymentInfo.setContent(plainText);
		baseMapper.insert(paymentInfo);
	}
	
	@Override
	public void createPaymentInfoForAlipay(Map<String, String> params) {
		log.info("记录支付日志");
		/**Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> params = gson.fromJson(plainText, HashMap.class);*/
		//- 商户订单号
		String orderNo = params.get("out_trade_no");
		//- 支付宝支付订单号
		String transactionId = params.get("trade_no");//区别于微信
		//- 交易类型
		//String tradeType = params.get("trade_type");//直接写
		//- 交易状态
		String tradeStatus = params.get("trade_status");
		log.info("交易状态---> {}" , tradeStatus);
		//- 订单金额
		String totalAmount = params.get("total_amount");
		//String先转成BigDecimal 改为分(数据库单位为分) 再转成int
		int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();//返回金额
		/**Map<String, Object> amount = (Map)params.get("amount");
		//坑:不能直接转为int(Gson缺陷) || 实际中应转换为BigDecimal(不要转为Double,会有精度损失[此处支付金额单位为(分)应该没事?])
		//用户实际支付金额
		//Integer payerTotal = ((BigDecimal) amount.get("payer_total")).intValue();//报错
		Integer payerTotal = ((Double) amount.get("payer_total")).intValue();*/
		log.info("订单实际金额---> {}" , totalAmount);
		
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setOrderNo(orderNo);
		paymentInfo.setPaymentType(PayType.ALIPAY.getType());
		paymentInfo.setTransactionId(transactionId);
		paymentInfo.setTradeType("电脑网站支付");
		paymentInfo.setTradeState(tradeStatus);
		paymentInfo.setPayerTotal(totalAmountInt);
		
		//改为JSON格式
		String json = new Gson().toJson(params, HashMap.class);
		//备份返回报文(各支付厂商可能返回类型不同,以备不时之需)
		paymentInfo.setContent(json);
		
		baseMapper.insert(paymentInfo);
	}
}
