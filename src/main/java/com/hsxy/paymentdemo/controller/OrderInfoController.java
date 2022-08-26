package com.hsxy.paymentdemo.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.hsxy.paymentdemo.vo.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @name OrderInfoController
 * @Description 控制器:商品订单管理
 * @author WU
 * @Date 2022/8/25 17:28
 */
@CrossOrigin //开放前端的跨域访问
@Api(tags = "商品订单管理")
@RestController
@RequestMapping("/api/order-info")
public class OrderInfoController {
	@Resource
	private OrderInfoService orderInfoService;
	@ApiOperation("订单列表 list")
	@GetMapping("/list")
	public AjaxResult list(){
		List<OrderInfo> list = orderInfoService.listOrderByCreateTimeDesc();
		return AjaxResult.ok().data("list", list);
	}
	
	@ApiOperation("查询本地订单状态")
	@GetMapping("/query-order-status/{orderNo}")
	public AjaxResult queryOrderStatus(@PathVariable String orderNo) {
		String orderStatus = orderInfoService.getOrderStatus(orderNo);
		if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {//支付成功
			return AjaxResult.ok();
		}
		return AjaxResult.ok().setCode(101).setMessage("支付中...");
	}
}
