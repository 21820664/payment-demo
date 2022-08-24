package com.hsxy.paymentdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
//@TableName("t_product")
public class Product extends BaseEntity{

    private String title; //商品名称

    private Integer price; //价格（分）{在mysql中，金额用“DECIMAL”类型}
}
