package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.config.WxPayConfig;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @name TestController
 * @Description 控制器:测试
 * @author WU
 * @Date 2022/8/24 9:33
 */
@Api(tags = "测试控制器")
@RestController
@RequestMapping("/api/test")
public class TestController {
	@Resource
	private WxPayConfig wxPayConfig;
	@GetMapping("/get-wx-pay-config")
	public AjaxResult getWxPayConfig(){
		String mchId = wxPayConfig.getMchId();
		return AjaxResult.ok().data("mchId", mchId);
	}
}