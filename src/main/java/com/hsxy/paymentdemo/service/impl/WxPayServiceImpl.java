package com.hsxy.paymentdemo.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import com.hsxy.paymentdemo.config.WxPayConfig;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.RefundInfo;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.enums.PayType;
import com.hsxy.paymentdemo.enums.wxpay.WxApiType;
import com.hsxy.paymentdemo.enums.wxpay.WxNotifyType;
import com.hsxy.paymentdemo.enums.wxpay.WxRefundStatus;
import com.hsxy.paymentdemo.enums.wxpay.WxTradeState;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.service.PaymentInfoService;
import com.hsxy.paymentdemo.service.RefundInfoService;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.util.HttpClientUtils;
import com.hsxy.paymentdemo.util.HttpUtils;
import com.hsxy.paymentdemo.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
		OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId, PayType.WXPAY.getType());
		
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
		// TODO WU: 公共参数最好定义常量(支付宝做得好:AlipayConstants)
		paramsMap.put("appid", wxPayConfig.getAppid());
		paramsMap.put("mchid", wxPayConfig.getMchId());
		paramsMap.put("description", orderInfo.getTitle());
		paramsMap.put("out_trade_no", orderInfo.getOrderNo());
		paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));// DO WU: 避免输入一大串:https://wxpay.loca.lt/api/wx-pay/native/notify
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
				/*try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				
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
	 * @Description 微信支付端关单接口的调用
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
	
	@Override
	public String queryOrder(String orderNo) throws IOException {
		log.info("查单接口调用 ===> {}", orderNo);
		String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
		url = wxPayConfig.getDomain().concat(url).concat("?mchid=").concat(wxPayConfig.getMchId());
		HttpGet httpGet = new HttpGet(url);
		//区别于POST方式,只需设置请求头格式(JSON)
		httpGet.setHeader("Accept", "application/json");
		//完成签名并执行请求(与创建订单，调用Native支付接口相似)
		// TODO WU: 考虑合并重复代码为方法
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
			int statusCode = response.getStatusLine().getStatusCode();//响应状态码
			if (statusCode == 200) { //处理成功
				log.info("成功, 返回结果 = " + bodyAsString);
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode+ ",返回结果 = " +
						bodyAsString);
				throw new IOException("request failed");
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
	
	@Override
	public void checkOrderStatus(String orderNo) throws Exception {
		log.warn("根据订单号核实订单状态 ===> {}", orderNo);
		//调用微信支付查单接口
		String result = this.queryOrder(orderNo);
		Gson gson = new Gson();
		Map resultMap = gson.fromJson(result, HashMap.class);
		//获取微信支付端的订单状态
		Object tradeState = resultMap.get("trade_state");
		//判断订单状态
		if(WxTradeState.SUCCESS.getType().equals(tradeState)){
			log.warn("核实订单已支付 ===> {}", orderNo);
			//如果确认订单已支付则更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
			//记录支付日志
			paymentInfoService.createPaymentInfo(result);
		}
		if(WxTradeState.NOTPAY.getType().equals(tradeState)){
			log.warn("核实订单未支付 ===> {}", orderNo);
			//如果订单未支付，则调用关单接口
			this.closeOrder(orderNo);
			//更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
	}
	
	@Resource
	private RefundInfoService refundsInfoService;

	/**
	 * @Description 退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void refund(String orderNo, String reason) throws IOException {
		log.info("创建退款单记录");
		//根据订单编号创建退款单
		RefundInfo refundsInfo = refundsInfoService.createRefundByOrderNo(orderNo, reason);
		log.info("调用退款API");
		//调用统一下单API
		String url = wxPayConfig.getDomain().concat(WxApiType.DOMESTIC_REFUNDS.getType());
		HttpPost httpPost = new HttpPost(url);
		// 请求body参数
		Gson gson = new Gson();
		Map paramsMap = new HashMap();
		paramsMap.put("out_trade_no", orderNo);//订单编号
		paramsMap.put("out_refund_no", refundsInfo.getRefundNo());//退款单编号
		paramsMap.put("reason",reason);//退款原因
		paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));//退款通知地址
		//-| 金额
		Map amountMap = new HashMap();
		amountMap.put("refund", refundsInfo.getRefund());//退款金额
		amountMap.put("total", refundsInfo.getTotalFee());//原订单金额
		amountMap.put("currency", "CNY");//退款币种
		paramsMap.put("amount", amountMap);
		//将参数转换成json字符串
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数 ===> {}" + jsonParams);
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");//设置请求报文格式
		httpPost.setEntity(entity);//将请求报文放入请求对象
		httpPost.setHeader("Accept", "application/json");//设置响应报文格式
		//完成签名并执行请求，并完成验签
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
		try {
			//解析响应结果
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 退款返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("退款异常, 响应码 = " + statusCode+ ", 退款返回结果 = " + bodyAsString);
			}
			//更新订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_PROCESSING);
			//更新退款单
			refundsInfoService.updateRefund(bodyAsString);
		} finally {
			response.close();
		}
	}
	
	@Override
	public String queryRefund(String refundNo) throws IOException {
		log.info("查询退款接口调用 ===> {}", refundNo);
		String url = String.format(WxApiType.DOMESTIC_REFUNDS_QUERY.getType(), refundNo);
		url = wxPayConfig.getDomain().concat(url);
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept", "application/json");
		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 查询退款返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("查询退款异常, 响应码 = " + statusCode+ ",查询退款返回结果 = " + bodyAsString);
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
	
	@Override
	public void checkRefundStatus(String refundNo) throws IOException {
		log.warn("根据退款单号核实退款单状态 ===> {}", refundNo);
		//调用查询退款单接口
		String result = this.queryRefund(refundNo);
		//组装json请求体字符串
		Gson gson = new Gson();
		Map<String,String> resultMap = gson.fromJson(result, HashMap.class);
		//获取微信支付端退款状态
		//Object tradeState = resultMap.get("trade_state");
		String status = resultMap.get("status");//退款状态
		String orderNo = resultMap.get("out_trade_no");//订单号
		
		//判断退款单状态
		if(WxRefundStatus.SUCCESS.getType().equals(status)){
			log.warn("核实订单已退款成功 ===> {}", refundNo);
			//如果确认退款成功，则更新**订单状态**
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
			//更新退款单
			refundsInfoService.updateRefund(result);
		}
		if(WxRefundStatus.ABNORMAL.getType().equals(status)){
			log.warn("核实订单退款异常 ===> {}", refundNo);
			//如果确认退款异常，则更新**订单状态**
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_ABNORMAL);
			//更新退款单
			refundsInfoService.updateRefund(result);
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void processRefund(String plainText) {
		log.info("退款单");
		
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
				//<**与处理订单的区别**>
				if (!OrderStatus.REFUND_PROCESSING.getType().equals(orderStatus)) {
					return;
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
				/*//记录支付日志
				paymentInfoService.createPaymentInfo(plainText);*/
				//更新退款单<**与处理订单的区别**>
				refundsInfoService.updateRefund(plainText);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
	
	@Override
	public String queryBill(String billDate, String type) throws IOException {
		log.warn("申请账单接口调用,日期: {}", billDate);
		String url;//不用初始化
		//微信支付提供,写死了
		if("tradebill".equals(type)){
			url = WxApiType.TRADE_BILLS.getType();
		}else if("fundflowbill".equals(type)){
			url = WxApiType.FUND_FLOW_BILLS.getType();
		}else{
			throw new RuntimeException("不支持的账单类型");
		}
		
		url = wxPayConfig.getDomain().concat(url).concat("?bill_date=").concat(billDate);
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept", "application/json");
		
		//使用wxPayClient发送请求得到响应
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 申请账单返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("申请账单异常, 响应码 = " + statusCode+ ",申请账单返回结果 = " + bodyAsString);
			}
			
			//获取账单下载地址
			/*Gson gson = new Gson();
			Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
			return resultMap.get("download_url");*/
			
			return (String)new Gson().fromJson(bodyAsString,HashMap.class).get("download_url");
			
		} finally {
			response.close();
		}
	}
	
	@Resource
	private CloseableHttpClient wxPayNoSignClient; //无需应答签名
	
	@Override
	public String downloadBill(String billDate, String type) throws IOException {
		log.warn("下载账单接口调用,日期: {},账单类型: {}", billDate, type);
		//获取账单url地址
		String downloadUrl = this.queryBill(billDate, type);
		
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(downloadUrl);
		httpGet.addHeader("Accept", "application/json");
		//使用wxPayClient发送请求得到响应
		CloseableHttpResponse response = wxPayNoSignClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 下载账单返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("下载账单异常, 响应码 = " + statusCode+ ",下载账单返回结果 = " + bodyAsString);
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
	
	@Override
	public Map<String, Object> nativePayV2(Long productId, String remoteAddr) throws Exception {
		log.info("生成订单");
		//生成订单<与V3一致>
		OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId, PayType.WXPAY.getType());
		String codeUrl = orderInfo.getCodeUrl();
		if(orderInfo != null && !StringUtils.isEmpty(codeUrl)){
			log.info("订单已存在，二维码已保存");
			//返回二维码
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;
		}
		
		log.info("调用统一下单API");
		HttpClientUtils client = new
				HttpClientUtils("https://api.mch.weixin.qq.com/pay/unifiedorder");
		//组装接口参数
		Map<String, String> params = new HashMap<>();
		params.put("appid", wxPayConfig.getAppid());//关联的公众号的appid
		params.put("mch_id", wxPayConfig.getMchId());//商户号
		params.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
		params.put("body", orderInfo.getTitle());
		params.put("out_trade_no", orderInfo.getOrderNo());
		//注意，这里必须使用字符串类型的参数（总金额：分）
		String totalFee = orderInfo.getTotalFee() + "";
		params.put("total_fee", totalFee);
		params.put("spbill_create_ip", remoteAddr);
		params.put("notify_url",
				wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));
		params.put("trade_type", "NATIVE");
		//将参数转换成xml字符串格式：生成带有签名的xml格式字符串<V2依赖>
		String xmlParams = WXPayUtil.generateSignedXml(params, wxPayConfig.getPartnerKey());
		log.info("\n xmlParams：\n" + xmlParams);
		client.setXmlParam(xmlParams);//将参数放入请求对象的方法体
		client.setHttps(true);//使用https形式发送
		client.post();//发送请求
		String resultXml = client.getContent();//得到响应结果
		log.info("\n resultXml：\n" + resultXml);
		//将xml响应结果转成map对象
		Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
		//错误处理
		if("FAIL".equals(resultMap.get("return_code")) ||
				"FAIL".equals(resultMap.get("result_code"))){
			log.error("微信支付统一下单错误 ===> {} ", resultXml);
			throw new RuntimeException("微信支付统一下单错误");
		}
		//二维码
		codeUrl = resultMap.get("code_url");
		//保存二维码
		String orderNo = orderInfo.getOrderNo();
		orderInfoService.saveCodeUrl(orderNo, codeUrl);
		//返回二维码
		Map<String, Object> map = new HashMap<>();
		map.put("codeUrl", codeUrl);
		map.put("orderNo", orderInfo.getOrderNo());
		return map;
	}
	
	@Resource
	private WxPayService wxPayService;
	/*@Resource
	private WxPayConfig wxPayConfig;
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private PaymentInfoService paymentInfoService;
	private final ReentrantLock lock = new ReentrantLock();*/
	
	/**
	 * @Description V2支付通知:微信支付通过支付通知接口将用户支付成功消息通知给商户
	 * @Param [request]
	 * @return java.lang.String
	 */
	@PostMapping("/native/notify")
	public String wxNotify(HttpServletRequest request) throws Exception {
		log.info("微信发送的回调");
		Map<String, String> returnMap = new HashMap<>();//应答对象
		//处理通知参数
		String body = HttpUtils.readData(request);
		//验签<V2依赖>
		if(!WXPayUtil.isSignatureValid(body, wxPayConfig.getPartnerKey())) {
			log.error("通知验签失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "验签失败");
			//V2以XML方式传输
			return WXPayUtil.mapToXml(returnMap);
		}
		//解析xml数据
		Map<String, String> notifyMap = WXPayUtil.xmlToMap(body);
		//判断通信和业务是否成功
		if(!"SUCCESS".equals(notifyMap.get("return_code")) || !"SUCCESS".equals(notifyMap.get("result_code"))) {
			log.error("失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "失败");
			return WXPayUtil.mapToXml(returnMap);
		}
		//获取商户订单号
		String orderNo = notifyMap.get("out_trade_no");
		OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
		//并校验返回的订单金额是否与商户侧的订单金额一致
		if (orderInfo != null && orderInfo.getTotalFee() != Long.parseLong(notifyMap.get("total_fee"))) {
			log.error("金额校验失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "金额校验失败");
			return WXPayUtil.mapToXml(returnMap);
		}
		//处理订单
		if(lock.tryLock()){
			try {
				//处理重复的通知
				//接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的。
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				if(OrderStatus.NOTPAY.getType().equals(orderStatus)){
					//更新订单状态
					orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
					//记录支付日志
					paymentInfoService.createPaymentInfo(body);
				}
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		returnMap.put("return_code", "SUCCESS");
		returnMap.put("return_msg", "OK");
		String returnXml = WXPayUtil.mapToXml(returnMap);
		log.info("支付成功，已应答");
		return returnXml;
	}
}
