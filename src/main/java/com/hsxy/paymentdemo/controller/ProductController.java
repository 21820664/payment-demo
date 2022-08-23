package com.hsxy.paymentdemo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @name ProductController
 * @Description 控制层:商品接口
 * @author WU
 * @Date 2022/8/23 11:29
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin //跨域
public class ProductController {
	@GetMapping("/test")
	public String test(){
		return "hello";
	}
}