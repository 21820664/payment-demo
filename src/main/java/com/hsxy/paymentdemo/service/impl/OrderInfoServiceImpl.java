package com.hsxy.paymentdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.entity.Product;
import com.hsxy.paymentdemo.enums.OrderStatus;
import com.hsxy.paymentdemo.mapper.OrderInfoMapper;
import com.hsxy.paymentdemo.mapper.ProductMapper;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hsxy.paymentdemo.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
	
	@Resource
	private ProductMapper productMapper;
	
	@Override
	public OrderInfo createOrderByProductId(Long productId) {
		
		//查找已存在但未支付的订单
		OrderInfo orderInfo = this.getNoPayOrderByProductId(productId);
		if( orderInfo != null ){
			return orderInfo;
		}
		
		//获取商品信息
		Product product = productMapper.selectById(productId);
		
		//生成订单
		orderInfo = new OrderInfo();
		orderInfo.setTitle(product.getTitle());
		orderInfo.setOrderNo(OrderNoUtils.getOrderNo()); //订单号
		orderInfo.setProductId(productId);
		orderInfo.setTotalFee(product.getPrice()); //分
		orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
		
		//baseMapper <== private OrderInfoMapper orderInfoMapper;
		baseMapper.insert(orderInfo);
		
		return orderInfo;
	}
	
	/**
	 * @Description 根据商品id查询未支付订单, 防止重复创建订单对象
	 * @Param [productId]
	 * @return com.hsxy.paymentdemo.entity.OrderInfo
	 */
	private OrderInfo getNoPayOrderByProductId(Long productId) {
		//QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();//使用λ表达式方法
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getProductId, productId)
					.eq(OrderInfo::getOrderStatus, OrderStatus.NOTPAY.getType())
					//设计缺陷:没做登录系统,无法使用user_id
					// queryWrapper.eq("user_id", userId);
					//自己另加:还应该价格一致(避免优惠活动影响)
					.eq(OrderInfo::getTotalFee,productMapper.selectById(productId).getPrice())
					//判断订单创建时间是否小于2小时(避免短时间生成多个未支付订单)<微信支付订单二维码最长生效时间为2小时>
					.apply("TIMESTAMPDIFF(HOUR, create_time , now()) < 2")
					//增加查询效率，只查询一条	(和selectOne()搭配,不可少)
					.last("limit 1");
		return baseMapper.selectOne(queryWrapper);
	}
	
	@Override
	public void saveCodeUrl(String orderNo, String codeUrl) {
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCodeUrl(codeUrl);
		baseMapper.update(orderInfo, queryWrapper);
	}
	
	@Override
	public List<OrderInfo> listOrderByCreateTimeDesc() {
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.orderByDesc(OrderInfo::getCreateTime);
		return baseMapper.selectList(queryWrapper);
	}
	
	@Override
	public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
		log.info("更新订单状态 ===> {}", orderStatus.getType());
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderNo,orderNo);
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderStatus(orderStatus.getType());
		baseMapper.update(orderInfo, queryWrapper);
	}
	
}
