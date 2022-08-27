package com.hsxy.paymentdemo.service.impl;

import com.google.gson.Gson;
import com.hsxy.paymentdemo.config.WxPayConfig;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.enums.wxpay.WxApiType;
import com.hsxy.paymentdemo.enums.wxpay.WxNotifyType;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.PaymentInfoService;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @name WxPayServiceImpl
 * @Description
 * @author WU
 * @Date 2022/8/24 15:45
 */
@Slf4j
@Service
public class WxPayServiceImpl implements WxPayService {
	
	@Resource
	private WxPayConfig wxPayConfig;
	
	@Resource
	private CloseableHttpClient wxPayClient;
	
	@Resource
	private OrderInfoService orderInfoService;
	
	@Override
	public Map<String, Object> nativePay(Long productId) throws IOException {
		
		log.info("1.生成订单");
		OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);
		
		// DO WU: 存入数据库
		String codeUrl = orderInfo.getCodeUrl();
		if(!StringUtils.isBlank(codeUrl)){
			log.info("订单(二维码)已存在");
			//返回二维码
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;//返回二维码和订单编号
		}
		
		log.info("2.调用统一下单API");
		HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));//避免输入一大串:https://api.mch.weixin.qq.com/v3/pay/transactions/native
		// 请求body参数
		Gson gson = new Gson();
		//Map paramsMap = new HashMap();//尽量在初始化时使用泛型
		HashMap<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("appid", wxPayConfig.getAppid());
		paramsMap.put("mchid", wxPayConfig.getMchId());
		paramsMap.put("description", orderInfo.getTitle());
		paramsMap.put("out_trade_no", orderInfo.getOrderNo());
		paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));// DO WU: 避免输入一大串:https://7d92-115-171-63-135.ngrok.io/api/wx-pay/native/notify
		//嵌套类型,再创建一个HashMap
		//Map amountMap = new HashMap();
		HashMap<String, Object> amountMap = new HashMap<>();
		amountMap.put("total", orderInfo.getTotalFee());
		amountMap.put("currency", "CNY");
		//嵌套类型,返回到paramsMap
		paramsMap.put("amount", amountMap);
	//-| 开关单此处代码一致
		//将参数转换成json字符串
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数：" + jsonParams);
		
		//设置请求头格式(JSON)
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
	//-|
		try {
			//响应体
			String bodyAsString = EntityUtils.toString(response.getEntity());
			//响应状态码
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { //处理成功(此处状态码为微信提供,可以写死)
				log.info("成功, 返回结果 = " + bodyAsString);
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode+ ",返回结果 = " + bodyAsString);
				throw new IOException("request failed");
			}
			//响应结果
			Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
			//二维码
			codeUrl = resultMap.get("code_url");
			
			//保存二维码到数据库
			String orderNo = orderInfo.getOrderNo();
			orderInfoService.saveCodeUrl(orderNo,codeUrl);
			
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;//返回二维码和订单编号
		} finally {
			response.close();
		}
		
	}
	
	@Resource
	private PaymentInfoService paymentInfoService;
	/**
	 * @Description 锁
	 */
	private final ReentrantLock lock = new ReentrantLock();
	@Override
	public void processOrder(String plainText) {
		log.info("处理订单");
		
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
		//商户订单号
		String orderNo = (String) plainTextMap.get("out_trade_no");
		
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
				
				//模拟通知并发
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
				//记录支付日志
				paymentInfoService.createPaymentInfo(plainText);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
	
	@Override
	public void cancelOrder(String orderNo) throws Exception {
		//调用微信支付的关单接口
		this.closeOrder(orderNo);
		//更新商户端的订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
	}
	
	/**
	 * @Description 关单接口的调用
	 * @Param [orderNo]
	 * @return void
	 */
	private void closeOrder(String orderNo) throws Exception {
		log.info("关单接口的调用，订单号 ===> {}", orderNo);
		//创建远程请求对象
		//String.format()双参数,第二个参数可替换第一个参数中的占位符
		String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
		url = wxPayConfig.getDomain().concat(url);
		HttpPost httpPost = new HttpPost(url);
		
		Gson gson = new Gson();
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("mchid", wxPayConfig.getMchId());
	//-| 开关单此处代码一致
		//将参数转换成json字符串
		//组装json请求体
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数 ===> {}", jsonParams);
		
		//设置请求头格式(JSON)
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		
		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
	//-|
		try {
			int statusCode = response.getStatusLine().getStatusCode();//响应状态码
			if (statusCode == 200) { //处理成功
				log.info("成功200");
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功204");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode);
				throw new IOException("request failed");
			}
		} finally {
			response.close();
		}
	}
	
}
