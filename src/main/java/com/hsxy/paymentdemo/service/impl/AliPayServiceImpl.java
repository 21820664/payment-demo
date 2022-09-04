package com.hsxy.paymentdemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.enums.PayType;
import com.hsxy.paymentdemo.enums.alipay.AliTradeState;
import com.hsxy.paymentdemo.service.AliPayService;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @name AliPayServiceImpl
 * @Description
 * @author WU
 * @Date 2022/8/31 17:51
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
	
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private AlipayClient alipayClient;
	//自动从上下文中获取加载过的文件
	@Resource
	private Environment config;
	
	@Transactional(rollbackFor = Exception.class)//需显示指定rollback
	@Override
	public String tradeCreate(Long productId) {
		try {
			//1.生成订单
			log.info("生成订单");
			OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId, PayType.ALIPAY.getType());
			//log.info(config.getProperty("wxpay.domain"));//可以获取微信的配置
			//调用支付宝接口
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			//配置需要的公共请求参数
			//通知回调接口:避免输入一大串(与微信不同,一体化简化长度)
			// 微信 :https://wxpay.loca.lt/api/wx-pay/native/notify
			//支付宝:https://alipay.loca.lt/api/ali-pay/trade/notify [!]支付通知需和尾部一致
			request.setNotifyUrl(config.getProperty("alipay.notify-url"));
			
			//支付完成后，我们想让页面跳转回支付成功的页面，配置returnUrl
			request.setReturnUrl(config.getProperty("alipay.return-url"));
			
			//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
			//先转成String再转成BigDecimal
			BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
			/**int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();*/

				/**组装当前业务方法的请求参数
				JSONObject bizContent = new JSONObject();
				bizContent.put("out_trade_no", orderInfo.getOrderNo());
				//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
				//先转成String再转成BigDecimal
				BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
				bizContent.put("total_amount", total);//订单总金额
				bizContent.put("subject", orderInfo.getTitle());//订单标题
				bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//销售产品码
			//将bizContent从JSONObject --> JSON(String)
			request.setBizContent(bizContent.toString())*/;
			
			//另解(支付宝封装,不易出错)
			AlipayTradePagePayModel model = new AlipayTradePagePayModel();
			model.setOutTradeNo(orderInfo.getOrderNo());
			model.setTotalAmount(total.toString());//需用回String
			model.setSubject(orderInfo.getTitle());
			model.setProductCode("FAST_INSTANT_TRADE_PAY");//销售产品码(当前仅支持该常量:新快捷即时到账产品)
			request.setBizModel(model);
			
			
			//执行请求，调用支付宝接口
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			if(response.isSuccess()){
				log.info("调用成功，返回结果 ===> " + response.getBody());
				return response.getBody();
			} else {
				log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
				throw new RuntimeException("创建支付交易失败");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new RuntimeException("创建支付交易失败");
		}
	}
	
	@Resource
	private PaymentInfoService paymentInfoService;

	/**
	 * @Description 锁
	 */
	private final ReentrantLock lock = new ReentrantLock();
	@Override
	public void processOrder(Map<String, String> params) {

		log.info("处理订单");
		/**支付宝无需转换,返回参数params即为Map
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);*/
		//商户订单号
		String orderNo = params.get("out_trade_no");
		
		/*在对业务数据进行状态检查和处理之前，
		要采用数据锁进行并发控制，
		以避免函数重入造成的数据混乱*/
		//尝试获取锁：
		// 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
		if(lock.tryLock()){
			try {
				//处理重复通知
				//保证接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
					return;
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
				//记录支付日志
				paymentInfoService.createPaymentInfoForAlipay(params);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
	
	@Override
	public void cancelOrder(String orderNo) throws Exception {
		//调用支付宝支付的关单接口
		this.closeOrder(orderNo);
		//更新商户端的订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
	}
	
	/**
	 * @Description 支付宝支付端关单接口的调用
	 * @Param [orderNo]
	 * @return void
	 */
	private void closeOrder(String orderNo) throws Exception {
		log.info("关单接口的调用，订单号 ===> {}", orderNo);
		//复制请求示例:https://opendocs.alipay.com/open/028wob?ref=api
		AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
		JSONObject bizContent = new JSONObject();
		//此时还未付款只有商户订单号(不用trade_no)
		bizContent.put("out_trade_no", orderNo);
		request.setBizContent(bizContent.toString());
		AlipayTradeCloseResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("调用成功，返回结果 ===> " + response.getBody());
		} else {
			log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
			//当未登陆支付宝或未扫码时,支付宝未创建订单,也就没有取消订单的说法了
			//throw new RuntimeException("关单接口的调用失败");
		}
		
	}
	
	@Override
	public String queryOrder(String orderNo) throws AlipayApiException {
		log.info("查单接口调用 ===> {}", orderNo);
		
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		JSONObject bizContent = new JSONObject();
		bizContent.put("out_trade_no", orderNo);
		request.setBizContent(bizContent.toString());
		AlipayTradeQueryResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("成功, 返回结果 = " + response.getBody());
			return response.getBody();
		} else {
			log.warn("调用失败,响应码 = " + response.getCode() + ",返回结果 = " + response.getBody());
			//throw new AlipayApiException("查单接口调用失败");
			//交易不存在
			return null;
		}
	}
	
	@Override
	public void checkOrderStatus(String orderNo) throws Exception {
		log.warn("根据订单号核实订单状态 ===> {}", orderNo);
		//调用支付宝支付查单接口
		String result = this.queryOrder(orderNo);
		//判断订单状态
		//1.订单未创建
		if(result == null){
			log.warn("核实订单未创建 ===> {}", orderNo);
			//只更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
		//根据支付宝文档提供的响应JSON
		HashMap<String, LinkedTreeMap> resultMap = new Gson().fromJson(result, HashMap.class);
			//获取嵌套JSON数据
		LinkedTreeMap alipayTradeQueryResponse = resultMap.get("alipay_trade_query_response");
		//获取微信支付端的订单状态
		String tradeStatus = (String)alipayTradeQueryResponse.get("trade_status");
		
		//2.订单未支付
		if(AliTradeState.WAIT_BUYER_PAY.getType().equals(tradeStatus)){
			log.warn("核实订单未支付 ===> {}", orderNo);
			//如果订单未支付，则调用关单接口
			this.closeOrder(orderNo);
			//更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
		//3.订单已支付
		else if(AliTradeState.TRADE_SUCCESS.getType().equals(tradeStatus)){
			log.warn("核实订单已支付 ===> {}", orderNo);
			//如果确认订单已支付则更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
			//记录支付日志(从嵌套JSON中获取)
			paymentInfoService.createPaymentInfoForAlipay(alipayTradeQueryResponse);
		}
		
	}
}
