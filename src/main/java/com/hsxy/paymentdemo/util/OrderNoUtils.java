package com.hsxy.paymentdemo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 订单号工具类
 *
 * @author qy/HSXY
 * @since 1.1
 */
public class OrderNoUtils {

    /**
     * 获取订单编号
     * @return
     */
    public static String getOrderNo() {
        return "HSXY_ORDER_" + getNo();
    }

    /**
     * 获取退款单编号
     * @return
     */
    public static String getRefundNo() {
        return "HSXY_REFUND_" + getNo();
    }

    /**
     * 获取编号算法(根据时间伪随机生成)
     * @return
     */
    public static String getNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;
    }

}
