package com.hsxy.paymentdemo;

import com.alibaba.fastjson.JSON;
import com.hsxy.paymentdemo.config.WxPayConfig;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.PrivateKey;

@SpringBootTest
class PaymentDemoApplicationTests {
	@Resource
	private WxPayConfig wxPayConfig;
	
	/**
	 * @Description 获取商户私钥
	 * @Param []
	 * @return void
	 */
/*	@Test
	void testGetPrivateKey(){
		//获取私钥路径
		String privateKeyPath = wxPayConfig.getPrivateKeyPath();
		//获取商户私钥(要测试前先改为public)
		PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);
		System.out.println(privateKey);//sun.security.rsa.RSAPrivateCrtKeyImpl@ffdd3c1b
		//引入fastjson依赖后可看到具体秘钥:
		System.out.println(JSON.toJSONString(privateKey));
		//{"algorithm":"RSA","algorithmId":{"name":"RSA","oID":{}},"crtCoefficient":...
	}*/
}
