package com.hsxy.paymentdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
	
}
