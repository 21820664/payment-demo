package com.hsxy.paymentdemo.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

/**
 * @name WxPayConfig
 * @Description
 * @author WU
 * @Date 2022/8/24 9:20
 */
@Configuration	//要增加Configuration依赖
@PropertySource("classpath:wxpay.properties") //读取配置文件(不从yml文件读取了)
@ConfigurationProperties(prefix="wxpay") //读取wxpay节点
@Data //使用set方法将wxpay节点中的值填充到当前类的属性中
public class WxPayConfig {
	
	// 商户号( Java文件与配置文件 的对应关系(小驼峰与中划线)自动映射,因此不用加注解映射)
	private String mchId;
	
	// 商户API证书序列号
	private String mchSerialNo;
	
	// 商户私钥文件
	private String privateKeyPath;
	
	// APIv3密钥
	private String apiV3Key;
	
	// APPID
	private String appid;
	
	// 微信服务器地址
	private String domain;
	
	// 接收结果通知地址
	private String notifyDomain;
	
	/**
	 * @Description 获取商户的私钥文件
	 * 先改成public方便进行测试
	 * @Param [filename] apiclient_key.pem
	 * @return java.security.PrivateKey
	 */
	private PrivateKey getPrivateKey(String filename){
		try {
			return PemUtil.loadPrivateKey(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("私钥文件不存在", e);
		}
	}
	
	/**
	 * @Description 获取签名验证器
	 * @Param []
	 * @return ScheduledUpdateCertificatesVerifier
	 */
	@Bean //便于自动执行(启动时一次<单例模式>)
	public Verifier getVerifier() throws GeneralSecurityException, IOException, HttpCodeException, NotFoundException {
		
		//获取商户私钥
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		//私钥签名对象（签名）
		PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo, privateKey);
		
		//身份认证对象（验签）
		WechatPay2Credentials wechatPay2Credentials = new
				WechatPay2Credentials(mchId, privateKeySigner);
		
		// 获取证书管理器实例
		CertificatesManager certificatesManager = CertificatesManager.getInstance();
		// 向证书管理器增加需要自动更新平台证书的商户信息
		/**
		 * 参数说明：
		 * merchantId:商户号-->mchId
		 * merchantSerialNumber:商户API证书的证书序列号-->mchSerialNo
		 * merchantPrivateKey:商户API私钥，如何加载商户API私钥请看常见问题-->privateKeyPath(地址)-->privateKey
		 * wechatPayCertificates:微信支付平台证书列表。你也可以使用后面章节提到的“定时更新平台证书功能”，而不需要关心平台证书的来龙去脉
		 * apiV3Key.getBytes(StandardCharsets.UTF_8):对称加密秘钥
		 * ---putMerchant需抛出异常
		 */
		//格式优化:(4.x版本)新版主要的不同就是这里，微信官方的SDK帮我们自动处理了验签之类的流程
		/*certificatesManager.putMerchant(mchId, new WechatPay2Credentials(mchId,
				new PrivateKeySigner(mchSerialNo, privateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));*/
		certificatesManager.putMerchant(mchId,wechatPay2Credentials,apiV3Key.getBytes(StandardCharsets.UTF_8));
		// 使用定时更新的签名验证器，不需要传入证书(3.x版本,淘汰)
		/*ScheduledUpdateCertificatesVerifier verifier = new ScheduledUpdateCertificatesVerifier(wechatPay2Credentials, apiV3Key.getBytes(StandardCharsets.UTF_8));*/
		// ... 若有多个商户号，可继续调用putMerchant添加商户信息
		// 向证书管理器增加需要自动更新平台证书的商户信息
		
		//从证书管理器中获取verifier
		return certificatesManager.getVerifier(mchId);
	}
	
	/**
	 * @Description 获取HttpClient对象
	 * @Param [verifier]
	 * @return org.apache.http.impl.client.CloseableHttpClient
	 */
	@Bean
	public CloseableHttpClient getWxPayClient(Verifier verifier){
		
		//获取商户私钥
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		
		//用于构造HttpClient
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(mchId, mchSerialNo, privateKey)
				.withValidator(new WechatPay2Validator(verifier));
		// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
		// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
		CloseableHttpClient httpClient = builder.build();
		return httpClient;
		
		// 后面跟使用Apache HttpClient一样
		//CloseableHttpResponse response = httpClient.execute(...);
	}
	
	
}
