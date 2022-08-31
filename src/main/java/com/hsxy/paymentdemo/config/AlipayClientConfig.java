package com.hsxy.paymentdemo.config;

import com.alipay.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @name AlipayClientConfig
 * @Description
 * @author WU
 * @Date 2022/8/31 16:37
 */
@Configuration
//加载配置文件
@PropertySource("classpath:alipay-sandbox.properties")
public class AlipayClientConfig {
	//与微信不同,使用更简便方法
	//自动获取alipay-sandbox.properties中的配置
	@Resource
	private Environment config;
	
	//使用支付宝支付SDK
	//https://opendocs.alipay.com/common/02kf5q
	@Bean
	public AlipayClient alipayClient() throws AlipayApiException {
		AlipayConfig alipayConfig = new AlipayConfig();
		//设置网关地址
		alipayConfig.setServerUrl(config.getProperty("alipay.gateway-url"));
		//设置应用Id
		alipayConfig.setAppId(config.getProperty("alipay.app-id"));
		//设置应用私钥
		alipayConfig.setPrivateKey(config.getProperty("alipay.merchant-privatekey"));
		//设置请求格式，固定常量值json
		alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
		//设置字符集
		alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
		//设置支付宝公钥
		alipayConfig.setAlipayPublicKey(config.getProperty("alipay.alipay-publickey"));
		//设置签名类型
		alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
		//构造client
		AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
		return alipayClient;
	}
	
	
}
