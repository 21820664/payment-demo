package com.hsxy.paymentdemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "商品管理 Product")
@RestController
@RequestMapping("/api/product")
@CrossOrigin //跨域
public class ProductController {
	
	@ApiOperation("Test接口")
	@GetMapping("/test")
	public String test(){
		return "hello";
	}
}