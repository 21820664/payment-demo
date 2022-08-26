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
}
