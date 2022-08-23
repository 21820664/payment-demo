package com.hsxy.paymentdemo.service.impl;

import com.hsxy.paymentdemo.entity.Product;
import com.hsxy.paymentdemo.mapper.ProductMapper;
import com.hsxy.paymentdemo.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
