package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.service.WxPayService;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @name WxPayV2Controller
 * @Description 控制器:网站微信支付V2版
 * @author WU
 * @Date 2022/8/29 17:52
 */
@CrossOrigin //跨域
@RestController
@RequestMapping("/api/wx-pay-v2")
@Api(tags = "网站微信支付APIv2")
@Slf4j
public class WxPayV2Controller {
	@Resource
	private WxPayService wxPayService;
	/**
	 * Native下单
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("调用统一下单API，生成支付二维码")
	@PostMapping("/native/{productId}")
	public AjaxResult createNative(@PathVariable Long productId, HttpServletRequest request) throws Exception {
		log.info("发起支付请求 v2");
		String remoteAddr = request.getRemoteAddr();
		Map<String, Object> map = wxPayService.nativePayV2(productId, remoteAddr);
		return AjaxResult.ok().setData(map);
	}
}
