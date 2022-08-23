package com.hsxy.paymentdemo.service.impl;

import com.hsxy.paymentdemo.entity.PaymentInfo;
import com.hsxy.paymentdemo.mapper.PaymentInfoMapper;
import com.hsxy.paymentdemo.service.PaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

}
