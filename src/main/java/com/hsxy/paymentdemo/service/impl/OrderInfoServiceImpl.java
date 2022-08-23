package com.hsxy.paymentdemo.service.impl;

import com.hsxy.paymentdemo.entity.OrderInfo;
import com.hsxy.paymentdemo.mapper.OrderInfoMapper;
import com.hsxy.paymentdemo.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

}
