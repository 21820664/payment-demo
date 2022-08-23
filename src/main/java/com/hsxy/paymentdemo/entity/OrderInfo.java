package com.hsxy.paymentdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
//@TableName("t_order_info")  //与数据库表名映射,也可在配置中加入:mybatis-plus.global-config.db-config.table-prefix=t_
public class OrderInfo  extends BaseEntity{

    private String title;//订单标题
    //mybatis-plus默认转换 属性与列名 的对应关系(小驼峰与下划线)因此不用加注解映射
    private String orderNo;//商户订单编号

    private Long userId;//用户id

    private Long productId;//支付产品id

    private Integer totalFee;//订单金额(分)

    private String codeUrl;//订单二维码连接

    private String orderStatus;//订单状态
}
