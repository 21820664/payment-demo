package com.hsxy.paymentdemo.config;

import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
}
