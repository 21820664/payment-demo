package com.hsxy.paymentdemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @name MybatisPlusConfig
 * @Description
 * @author WU
 * @Date 2022/8/23 16:25
 */
@Configuration
@MapperScan("com.hsxy.paymentdemo.mapper") //持久层扫描
@EnableTransactionManagement //启用事务管理
public class MybatisPlusConfig {
}
