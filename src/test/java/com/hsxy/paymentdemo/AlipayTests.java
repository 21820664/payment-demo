package com.hsxy.paymentdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @name AlipayTests
 * @Description
 * @author WU
 * @Date 2022/8/31 16:39
 */
@SpringBootTest
@Slf4j
class AlipayTests {
	
	//自动从上下文中获取加载过的文件
	@Resource
	private Environment config;
	@Test
	void testGetAlipayConfig(){
		log.info("appid = " + config.getProperty("alipay.app-id"));
	}
}