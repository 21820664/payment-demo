package com.hsxy.paymentdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling    //任务调度【开启调度】
public class PaymentDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PaymentDemoApplication.class, args);
	}
	
}
