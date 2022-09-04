# WechatPay

**流程图**

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/1661217853-1f5078a2a7efcbb17228dcb8eca4d812.png" alt="img" style="zoom:67%;" />

## 1、微信支付产品介绍

[微信支付官方介绍](https://pay.weixin.qq.com/static/product/product_intro.shtml?name=qrcode)

### 1.1 、付款码支付

用户展示微信钱包内的“付款码”给商家，商家扫描后直接完成支付，适用于线下面对面收银的场景。

### 1.2 、JSAPI支付

指商户通过调用微信支付提供的JSAPI接口，在支付场景中调起微信支付模块完成收款。[开发文档](https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter1_1_1.shtml)

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image002.gif)线下场所：商户展示一个支付二维码，用户使用微信扫描二维码后，输入需要支付的金额，完成支 付。

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image003.gif)公众号场景：用户在微信内进入商家公众号，打开某个页面，选择某个产品，完成支付。

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image004.gif)PC网站场景：在网站中展示二维码，用户使用微信扫描二维码，输入需要支付的金额，完成支付。

>   特点：用户在客户端==输入==支付金额

### 1.3 、小程序支付

在微信小程序平台内实现支付的功能。

### 1.4 、Native支付

Native支付是指商户展示支付二维码，用户再用微信“扫一扫”完成支付的模式。这种方式适用于PC网站。

>   特点：商家预先==指定==支付金额

### 1.5 、APP支付

商户通过在移动端独立的APP应用程序中集成微信支付模块，完成支付。

### 1.6 、刷脸支付

用户在刷脸设备前通过摄像头刷脸、识别身份后进行的一种支付方式。

## [项目前提](https://www.bilibili.com/video/BV1US4y1D77m?p=3&spm_id_from=pageDriver)

> 是为了获取：标识商户身份的信息、商户的证书和私钥、微信支付的证书、微信支付API的URL

*   1.获取商户号

    微信商户平台：[https://pay.weixin.qq.com/](https://pay.weixin.qq.com/) 步骤：申请成为商户 => 提交资料 => 签署协议 => 获取商户号

*   2.获取AppID

    微信公众平台：[https://mp.weixin.qq.com/](https://mp.weixin.qq.com/) 步骤：注册服务号 => 服务号认证 => 获取APPID => 绑定商户号

*   3.申请商户证书

    步骤：登录商户平台 => 选择 账户中心 => 安全中心 => API安全 => 申请API证书 包括商户证书和商户私钥

*   4.获取微信的证书

    可以预先下载，也可以通过编程的方式获取。

*   5.获取APIv3秘钥（在微信支付回调通知和商户获取平台证书使用APIv3密钥）

    步骤：登录商户平台 => 选择 账户中心 => 安全中心 => API安全 => 设置APIv3密钥

## 二、支付安全（证书/秘钥/签名）

### 1、信息安全的基础 - 机密性

**明文：**加密前的消息叫“明文”（plain text)

**密文：**加密后的文本叫“密文”（cipher text)

**密钥：**只有掌握特殊“钥匙”的人，才能对加密的文本进行解密，这里的“钥匙”就叫做“密钥”（key)

“密钥”就是一个字符串，度量单位是“位”（bit），比如，密钥长度是 128，就是 16 字节的二进制串

**加密：**实现机密性最常用的手段是“加密”（encrypt)

按照密钥的使用方式，加密可以分为两大类：对称加密和非对称加密。

**解密：**使用密钥还原明文的过程叫“解密”（decrypt) 

**加密算法：**加密解密的操作过程就是“加密算法”

所有的加密算法都是公开的，而算法使用的“密钥”则必须保密

### 2、对称加密和非对称加密

**对称加密**

特点：只使用一个密钥，密钥必须保密，常用的有 ==AES算法==

​	优点：运算速度快

缺点：秘钥需要信息交换的双方共享，一旦被窃取，消息会被破解，无法做到安全的密钥交换

>加密分组模式：将明文分组加密。微信支付中使用AEAP_AES_256_GCM

**非对称加密**

特点：使用两个密钥：公钥和私钥，公钥可以任意分发而私钥保密，常用的有 ==RSA==

优点：黑客获取公钥无法破解密文，解决了密钥交换的问题

缺点：运算速度非常慢

**混合加密**

实际场景中把对称加密和非对称加密结合起来使用。

 ![非对称加密](计算机网络面试#非对称加密)

### 3、身份认证

公钥加密，私钥解密的作用是加密信息

私钥加密，公钥解密的作用是身份认证

### 4、摘要算法（Digest Algorithm）

摘要算法就是我们常说的散列函数、哈希函数（Hash Function），它能够把任意长度的数据“压缩”成固定长度、而且独一无二的“摘要”字符串，就好像是给这段数据生成了一个数字“指纹”。

**作用**：

保证信息的完整性

**特性**：

不可逆：只有算法，没有秘钥，只能加密，不能解密难题

友好性：想要破解，只能暴力枚举

发散性：只要对原文进行一点点改动，摘要就会发生剧烈变化

抗碰撞性：原文不同，计算后的摘要也要不同

**常见摘要算法**：

==MD5==、SHA1、==SHA2==（SHA224、SHA256、SHA384）

### 5、数字签名

数字签名是使用私钥对摘要加密生成签名，需要由公钥将签名解密后进行验证，实现身份认证和不可否认

**签名和验证签名的流程**：

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image022.jpg)

 ![非对称加密](计算机网络面试#数字签名)

### 6、数字证书

数字证书解决“公钥的信任”问题，可以防止黑客伪造公钥。

不能直接分发公钥，公钥的分发必须使用数字证书，数字证书由CA颁发

**https**协议中的数字证书：

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image024.jpg) 

### 7、微信APIv3证书

**商户证书**：

商户API证书是指由商户申请的，包含商户的商户号、公司名称、公钥信息的证书。

商户证书在商户后台申请：[https://pay.weixin.qq.com/index.php/core/cert/api_cert#/](https://pay.weixin.qq.com/index.php/core/cert/api_cert%23/)

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image026.jpg)

**平台证书（微信支付平台）：**

微信支付平台证书是指由微信支付 负责申请的，包含微信支付平台标识、公钥信息的证书。商户可以使用平台证书中的公钥进行验签。

平台证书的获取：https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay3_0.shtml

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/clip_image028.jpg) 

### 8、API密钥和APIv3密钥

都是==对称==加密需要使用的加密和解密密钥，一定要保管好，不能泄露。

API密钥对应V2版本的API APIv3密钥对应V3版本的API

## 三、案例项目的创建

### 创建SpringBoot项目

### 1.0 、新建项目

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823110322906.png" alt="image-20220823110322906" style="zoom:50%;" />

>   注意：Java版本选择8
>
>   Server URL: http://start.aliyun.com

#### 1.1 、添加依赖

添加SpringBoot web依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 1.2 、配置application.yml文件

```yaml
server:
  port: 8090 #服务端口

spring: 
  application:
    name: payment-demo # 应用名称
```

创建controller包，创建ProductController类

```java
package com.atguigu.paymentdemo.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/product")
@CrossOrigin //跨域
public class ProductController {
	@GetMapping("/test")
	public String test(){
		return "hello";
	}
}
```

测试访问：

http://localhost:8090/api/product/test

### 2.0 引入Swagger

作用：自动生成接口文档和测试页面。

依赖

```xml
<!--swagger-->
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.7.0</version>
</dependency>

<!--swagger ui-->
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.7.0</version>
</dependency>
```

Swagger配置文件
 创建conﬁg包，创建Swagger2Conﬁg类

```java
@Configuration
@EnableSwagger2
public class Swagger2Config {
	@Bean
	public Docket docket(){
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfoBuilder().title("微信支付案例接口文档").build());
	}
}
```

运行,进入测试界面http://localhost:8090/swagger-ui.html

Swagger注解
controller中可以添加常用注解

```java
@Api(tags="商品管理") //用在类上

@ApiOperation("测试接口") //用在方法上
```

![image-20220823115710681](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823115710681.png)

### 3、定义统一结果

作用：定义统一响应结果，为前端返回标准格式的数据。
3.1、引入lombok依赖
简化实体类的开发

```xml
<!--实体对象工具类：低版本idea需要安装lombok插件-->
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
</dependency>
```

3.2、创建R类
创建统一结果类vo.AjaxResult

```java
@Data
@AllArgsConstructor
public class AjaxResult {
	private Integer code;
	private String message;
	//不用private Object data;∵可能带多个对象?
	private Map<String, Object> data = new HashMap<>();
	
	public AjaxResult(int code, String msg)
	{
		this.code = code;
		this.message = msg;
		/*this.data("code", code);
		this.data("message", msg);*/
	}
	public static AjaxResult ok(){
		/*AjaxResult r = new AjaxResult();
		r.setCode(0);
		r.setMessage("成功");
		return r;*/
		return new AjaxResult(200,"成功");
	}
	
	public static AjaxResult error(){
		/*AjaxResult r = new AjaxResult();
		r.setCode(-1);
		r.setMessage("失败");
		return r;*/
		return new AjaxResult(-1,"失败");
	}
	
	/**
	 * @Description 方便链式调用
	 * @Param [key, value] {键,值}
	 * @return com.hsxy.paymentdemo.vo.AjaxResult {数据对象}
	 */
	public AjaxResult data(String key, Object value){
		//super.put(key, value);
		this.data.put(key, value);
		return this;
	}
}

```

修改controller
修改test方法，返回统一结果

```java
	@ApiOperation("Test接口")
	@GetMapping("/test")
	public AjaxResult test(){
		return AjaxResult.ok()
				.data("msg","hello")
				.data("now",new Date());
	}
```

配置json时间格式

```yaml
spring:
    application:
        name: payment-demo # 应用名称
    jackson: #json时间格式
        date-format: yyyy-MM-dd HH:mm:ss
        time-zone: GMT+8
```

### 4、创建数据库

```shell
mysql -uroot -p
mysql> create database payment_demo;
```

执行SQL脚本

```sql
USE `payment_demo`;

/*Table structure for table `t_order_info` */

CREATE TABLE `t_order_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `title` varchar(256) DEFAULT NULL COMMENT '订单标题',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `product_id` bigint(20) DEFAULT NULL COMMENT '支付产品id',
  `total_fee` int(11) DEFAULT NULL COMMENT '订单金额(分)',
  `code_url` varchar(50) DEFAULT NULL COMMENT '订单二维码连接',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


/*Table structure for table `t_payment_info` */

CREATE TABLE `t_payment_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '支付记录id',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '支付系统交易编号',
  `payment_type` varchar(20) DEFAULT NULL COMMENT '支付类型',
  `trade_type` varchar(20) DEFAULT NULL COMMENT '交易类型',
  `trade_state` varchar(50) DEFAULT NULL COMMENT '交易状态',
  `payer_total` int(11) DEFAULT NULL COMMENT '支付金额(分)',
  `content` text COMMENT '通知参数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


/*Table structure for table `t_product` */

CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `title` varchar(20) DEFAULT NULL COMMENT '商品名称',
  `price` int(11) DEFAULT NULL COMMENT '价格（分）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_product` */

insert  into `t_product`(`title`,`price`) values ('Java课程',1);
insert  into `t_product`(`title`,`price`) values ('大数据课程',1);
insert  into `t_product`(`title`,`price`) values ('前端课程',1);
insert  into `t_product`(`title`,`price`) values ('UI课程',1);

/*Table structure for table `t_refund_info` */

CREATE TABLE `t_refund_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '退款单id',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `refund_no` varchar(50) DEFAULT NULL COMMENT '商户退款单编号',
  `refund_id` varchar(50) DEFAULT NULL COMMENT '支付系统退款单号',
  `total_fee` int(11) DEFAULT NULL COMMENT '原订单金额(分)',
  `refund` int(11) DEFAULT NULL COMMENT '退款金额(分)',
  `reason` varchar(50) DEFAULT NULL COMMENT '退款原因',
  `refund_status` varchar(10) DEFAULT NULL COMMENT '退款状态',
  `content_return` text COMMENT '申请退款返回参数',
  `content_notify` text COMMENT '退款结果通知参数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```

### 5、集成MyBatis-Plus

5.1、引入依赖

```xml
<!--mysql驱动-->
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
</dependency>
<!--持久层-->
<dependency>
	<groupId>com.baomidou</groupId>
	<artifactId>mybatis-plus-boot-starter</artifactId>
	<version>3.3.1</version>
</dependency>
```

5.2、配置数据库连接

```yaml
spring:
  # mysql数据库连接
  datasource:
    url: jdbc:mysql://localhost:3306/payment_demo?serverTimezone=GMT%2B8&characterEncoding=utf-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

5.3、定义实体类(就是dao/pojo)
BaseEntity是父类，其他类继承BaseEntity

![image-20220823153654971](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823153654971.png)

```java
@TableName("t_order_info")  //与数据库表名映射,也可在配置中加入:mybatis-plus.global-config.db-config.table-prefix=t_

 	//mybatis-plus默认转换 属性与列名 的对应关系(小驼峰与下划线)因此不用加注解映射
    private String orderNo;//商户订单编号
```

```yaml
mybatis-plus:
  configuration:
    #sql日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl 
  .global-config:
    db-config:
      #忽略数据库表头名
      table-prefix: t_
```

>   \payment\微信支付\04-资料\代码

5.4、定义持久层
定义Mapper接口继承 BaseMapper<>，
定义xml配置文件(仅定义,没写内容)

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823160556463.png" alt="image-20220823160556463" style="zoom: 50%;" />


5.5、定义MyBatis-Plus的配置文件
在config包中创建配置文件 MybatisPlusConfig

```java
@Configuration
@MapperScan("com.hsxy.paymentdemo.mapper") //持久层扫描
@EnableTransactionManagement //启用事务管理
public class MybatisPlusConfig {
}
```

5.6、定义yml配置文件
添加持久层日志和xml文件位置的配置

```yaml
mybatis-plus:
  configuration: #sql日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath:com/atguigu/paymentdemo/mapper/xml/*.xml #如果放入resources文件夹下则不用指定
```

 5.7、定义业务层

定义业务层接口继承 IService<>
定义业务层接口的实现类，并继承 ServiceImpl<,>

![image-20220823155028520](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823155028520.png)



>   ```java
>   public interface OrderInfoService extends IService<OrderInfo> {
>   }
>   
>   @Service
>   public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
>   }
>   ```
>
>   `IService`:Mybatis-plus提供的预定义service,定义了很多抽象方法
>
>   `ServiceImpl`:实现了刚才的IService

5.8、定义接口方法查询所有商品
在 public class ProductController 中添加一个方法

```java
@Resource
private ProductService productService;

@ApiOperation("商品列表")
@GetMapping("/list")
public AjaxResult list(){
	List<Product> list = productService.list();//直接调用IService的list方法
	return AjaxResult.ok().data("productList", list);
}
```

5.9、Swagger中测试

![image-20220823164212513](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823164212513.png)

5.10、pom中配置build节点
因为maven工程在==默认情况==下 src/main/java 目录下的==所有资源文件==是**不**发布到 target 目录下的，我们
在 pom 文件的 节点下配置一个资源发布过滤器

```xml
<!-- 项目打包时会将java目录中的*.xml文件也进行打包 -->
<resources>
	<resource>
		<directory>src/main/java</directory>
		<includes>
			<include>**/*.xml</include>
		</includes>
		<filtering>false</filtering>
	</resource>
</resources>
```

>   ⚠️此为非规范写法
>
>   正确方式是在resources文件夹下创建com/hsxy/paymentdemo/mapper,将xml文件移动到资源文件夹中,此时可发布到 target 目录下
>
>   <img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823164931265.png" alt="image-20220823164931265" style="zoom:50%;" />



### 6、搭建前端环境

6.1、安装Node.js
Node.js是一个基于JavaScript引擎的服务器端环境，前端项目在开发环境下要基于Node.js来运行
安装：node-v14.18.0-x64.msi
6.2、运行前端项目
将项目放在磁盘的一个目录中，例如 D:\demo\payment-demo-front
进入项目目录，运行下面的命令启动项目：

```shell
npm run serve
```

6.3、安装VSCode插件

![image-20220823155323314](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823155323314.png)

### 7、Vue.js入门
官网：https://cn.vuejs.org/
Vue.js是一个前端框架，帮助我们快速构建前端项目。
使用vue有两种方式，一个是传统的在 html 文件中引入 js 脚本文件的方式，另一个是脚手架的方式。
我们的项目，使用的是脚手架的方式。
7.1、安装脚手架
配置淘宝镜像

```shell
#经过下面的配置，所有的 npm install 都会经过淘宝的镜像地址下载
npm config set registry https://registry.npm.taobao.org
```

全局安装脚手架

```shell
npm install -g @vue/cli
```

7.2、创建一个项目
先进入项目目录（Ctrl + ~），然后创建一个项目

```shell
vue create vue-demo
```

>   如果vue无法识别需将其存在目录加入全局环境变量
>
>   vue也可在前端创建项目
>
>   ```shell
>   vue ui
>   ```
>
>   

7.3、运行项目

```shell
npm run serve
# 指定运行端口
npm run serve -- --port 8888
```

7.4、数据绑定
修改 src/App.vue

```vue
<!--定义页面结构-->
<template>
	<div>
		<h1>Vue案例</h1>
		<!-- 插值 -->
		<p>{{course}}</p>
	</div>
</template>
<!--定义页面脚本-->
<script>
export default {
	// 定义数据
	data () {
		return {
			course: '微信支付'
		}
	}
}
</script>
```

7.5、安装Vue调试工具
在Chrome的扩展程序中安装：Vue.jsDevtools.zip
（1）扩展程序的安装

![image-20220823155637212](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823155637212.png)

（2）扩展程序的使用

![image-20220823184316458](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220823184316458.png)

7.6、双向数据绑定
数据会绑定到组件，组件的改变也会影响数据定义

```html
<p>
	<!-- 指令 -->
	<input type="text" v-model="course">
</p>
```

7.7、事件处理
（1）定义事件

```js
// 定义方法
methods: {
	toPay(){
		console.log('去支付')
	}
}
```

（2）调用事件

```html
<p>
	<!-- 事件 -->
	<button @click="toPay()">去支付</button>
</p>
```

## 四、基础支付API V3

### 1、引入支付参数

1.1、定义微信支付相关参数
将资料文件夹中的 wxpay.properties 复制到resources目录中
这个文件定义了之前我们准备的微信支付相关的参数，例如商户号、APPID、API秘钥等等

```properties
# 微信支付相关参数
# 商户号
wxpay.mch-id=1558950191
# 商户API证书序列号
wxpay.mch-serial-no=34345964330B66427E0D3D28826C4993C77E631F
# 商户私钥文件
wxpay.private-key-path=apiclient_key.pem
# APIv3密钥
wxpay.api-v3-key=UDuLFDcmy5Eb6o0nTNZdu6ek4DDh4K8B
# APPID
wxpay.appid=wx74862e0dfcf69954
# 微信服务器地址
wxpay.domain=https://api.mch.weixin.qq.com
# 接收结果通知地址
wxpay.notify-domain=https://7d92-115-171-63-135.ngrok.io
```

![image-20220824091816049](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824091816049.png)

>   但是IDEA未识别为spring配置,会丢失部分功能(如无法点击定位)
>
>   解决方法:File -> Project Structure -> Modules -> 选择小叶子 -> 点击(+) -> 选择文件
>
>   ![image-20220824094605300](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824094605300.png)

1.2、读取支付参数
将资料文件夹中的 config 目录中的 WxPayConfig.java 复制到源码目录中。

```java
@Configuration	//要增加Configuration依赖
@PropertySource("classpath:wxpay.properties") //读取配置文件(不从yml文件读取了)
@ConfigurationProperties(prefix="wxpay") //读取wxpay节点
@Data //使用set方法将wxpay节点中的值填充到当前类的属性中
public class WxPayConfig {
	
	// 商户号( Java文件与配置文件 的对应关系(小驼峰与中划线)自动映射,因此不用加注解映射)
	private String mchId;
	
	// 商户API证书序列号
	private String mchSerialNo;
	
	// 商户私钥文件
	private String privateKeyPath;
	
	// APIv3密钥
	private String apiV3Key;
	
	// APPID
	private String appid;
	
	// 微信服务器地址
	private String domain;
	
	// 接收结果通知地址
	private String notifyDomain;
	
}
```

![image-20220824091924569](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824091924569.png)

出现警告:

![image-20220824092218233](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824092218233-16613046986494.png)

需引入依赖:

```xml
		<!--使用注解@Configuration标识配置类-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
```

1.3、测试支付参数的获取
在 controller 包中创建 TestController

```java
@Api(tags = "测试控制器")
@RestController
@RequestMapping("/api/test")
public class TestController {
	@Resource
	private WxPayConfig wxPayConfig;
	@GetMapping("/get-wx-pay-config")
	public AjaxResult getWxPayConfig(){
		String mchId = wxPayConfig.getMchId();
		return AjaxResult.ok().data("mchId", mchId);
	}
}
```

测试:

![image-20220824095225717](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824095225717.png)



### 2、加载商户私钥

2.1、复制商户私钥
将下载的私钥文件apiclient_key.pem复制到项目根目录下：

![image-20220824094947284](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824094947284.png)

2.2、引入SDK
https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay6_0.shtml
我们可以使用官方提供的 SDK，帮助我们完成开发。实现了请求签名的生成和应答签名的验证。

```xml
<!--wechatpay-sdk-->
<dependency>
    <groupId>com.github.wechatpay-apiv3</groupId>
    <artifactId>wechatpay-apache-httpclient</artifactId>
    <version>0.4.5</version>
</dependency>
```

2.3、获取商户私钥
https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient （如何加载商户私钥）

在WxPayConfig中增加获取方法

```java
	/**
	 * @Description 获取商户的私钥文件
	 * 先改成public方便进行测试
	 * @Param [filename] apiclient_key.pem
	 * @return java.security.PrivateKey
	 */
	private PrivateKey getPrivateKey(String filename){
		try {
			return PemUtil.loadPrivateKey(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("私钥文件不存在", e);
		}
	}
```

2.4、测试商户私钥的获取

在测试用例中的 PaymentDemoApplicationTests 测试类中添加如下方法，测试私钥对象是否能够获取出来。
（将前面的方法改成public的再进行测试）

```java
@SpringBootTest
class PaymentDemoApplicationTests {
	@Resource
	private WxPayConfig wxPayConfig;
	
	/**
	 * @Description 获取商户私钥
	 * @Param []
	 * @return void
	 */
	@Test
	void testGetPrivateKey(){
		//获取私钥路径
		String privateKeyPath = wxPayConfig.getPrivateKeyPath();
		//获取商户私钥(要测试前先改为public)
		PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);
		System.out.println(privateKey);//sun.security.rsa.RSAPrivateCrtKeyImpl@ffdd3c1b
		//引入fastjson依赖后可看到具体秘钥:
		System.out.println(JSON.toJSONString(privateKey));
		//{"algorithm":"RSA","algorithmId":{"name":"RSA","oID":{}},"crtCoefficient":...
	}
}
```

>   记得将getPrivateKey改回private

### 3、获取签名验证器和HttpClient

3.1、证书密钥使用说明
https://pay.weixin.qq.com/wiki/doc/apiv3_partner/wechatpay/wechatpay3_0.shtml

![image-20220824100049859](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824100049859.png)

3.2、获取签名验证器
 （[定时更新平台证书功能](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient#%E5%AE%9A%E6%97%B6%E6%9B%B4%E6%96%B0%E5%B9%B3%E5%8F%B0%E8%AF%81%E4%B9%A6%E5%8A%9F%E8%83%BD)）
平台证书：平台证书封装了微信的公钥，商户可以使用平台证书中的公钥进行验签。
签名验证器：帮助我们进行验签工作，我们单独将它定义出来，方便后面的开发。

示例代码:

```java
// 获取证书管理器实例
certificatesManager = CertificatesManager.getInstance();
// 向证书管理器增加需要自动更新平台证书的商户信息
certificatesManager.putMerchant(merchantId, new WechatPay2Credentials(merchantId,
            new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
// ... 若有多个商户号，可继续调用putMerchant添加商户信息

// 从证书管理器中获取verifier
verifier = certificatesManager.getVerifier(merchantId);
WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
        .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
        .withValidator(new WechatPay2Validator(verifier))
// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient

// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
CloseableHttpClient httpClient = builder.build();

// 后面跟使用Apache HttpClient一样
CloseableHttpResponse response = httpClient.execute(...);
```

这段需看P28 有改动

```java
	/**
	 * @Description 获取签名验证器
	 * @Param []
	 * @return ScheduledUpdateCertificatesVerifier
	 */
	@Bean //便于自动执行(启动时一次<单例模式>)
	public Verifier getVerifier() throws GeneralSecurityException, IOException, HttpCodeException, NotFoundException {
		
		//获取商户私钥
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		//私钥签名对象（签名）
		PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo, privateKey);
		
		//身份认证对象（验签）
		WechatPay2Credentials wechatPay2Credentials = new
				WechatPay2Credentials(mchId, privateKeySigner);
		
		// 获取证书管理器实例
		CertificatesManager certificatesManager = CertificatesManager.getInstance();
		// 向证书管理器增加需要自动更新平台证书的商户信息
		/**
		 * 参数说明：
		 * merchantId:商户号-->mchId
		 * merchantSerialNumber:商户API证书的证书序列号-->mchSerialNo
		 * merchantPrivateKey:商户API私钥，如何加载商户API私钥请看常见问题-->privateKeyPath(地址)-->privateKey
		 * wechatPayCertificates:微信支付平台证书列表。你也可以使用后面章节提到的“定时更新平台证书功能”，而不需要关心平台证书的来龙去脉
		 * apiV3Key.getBytes(StandardCharsets.UTF_8):对称加密秘钥
		 * ---putMerchant需抛出异常
		 */
		//格式优化:(4.x版本)新版主要的不同就是这里，微信官方的SDK帮我们自动处理了验签之类的流程
		/*certificatesManager.putMerchant(mchId, new WechatPay2Credentials(mchId,
				new PrivateKeySigner(mchSerialNo, privateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));*/
		certificatesManager.putMerchant(mchId,wechatPay2Credentials,apiV3Key.getBytes(StandardCharsets.UTF_8));
		// 使用定时更新的签名验证器，不需要传入证书(3.x版本,淘汰)
		/*ScheduledUpdateCertificatesVerifier verifier = new ScheduledUpdateCertificatesVerifier(wechatPay2Credentials, apiV3Key.getBytes(StandardCharsets.UTF_8));*/
		// ... 若有多个商户号，可继续调用putMerchant添加商户信息
		// 向证书管理器增加需要自动更新平台证书的商户信息
		
		//从证书管理器中获取verifier
		return certificatesManager.getVerifier(mchId);
	}
```

3.4、获取 HttpClient 对象
 （[定时更新平台证书功能](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient#%E5%AE%9A%E6%97%B6%E6%9B%B4%E6%96%B0%E5%B9%B3%E5%8F%B0%E8%AF%81%E4%B9%A6%E5%8A%9F%E8%83%BD)）
HttpClient 对象：是建立远程连接的基础，我们通过SDK创建这个对象。

```java
	/**
	 * @Description 获取HttpClient对象
	 * @Param [verifier]
	 * @return org.apache.http.impl.client.CloseableHttpClient
	 */
	@Bean
	public CloseableHttpClient getWxPayClient(Verifier verifier){
		
		//获取商户私钥
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		
		//用于构造HttpClient
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(mchId, mchSerialNo, privateKey)
				.withValidator(new WechatPay2Validator(verifier));
		// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
		// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
		CloseableHttpClient httpClient = builder.build();
		return httpClient;
		
		// 后面跟使用Apache HttpClient一样
		//CloseableHttpResponse response = httpClient.execute(...);
	}
```

>   ⚠️⚠️⚠️在编写后运行遇到BUG(查了好久,坑爹啊)
>
>   ![image-20220824203025666](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824203025666.png)
>
>   #### 产生错误原因
>
>   为了数据代码在传输过程中的安全，很多时候我们都会将要传输的数据进行加密，然后等对方拿到后再解密使用。我们在使用AES加解密的时候，在遇到128位密钥加解密的时候，没有进行什么特殊处理；然而，在使用256位密钥加解密的时候，如果不进行特殊处理的话，往往会出现这个异常java.security.InvalidKeyException: Illegal key size。
>
>   #### 为什么会产生这样的错误
>
>   我们做Java开发，或是Android开发，都会先在电脑上安装JDK(Java Development Kit) 并配置环境变量，JDK也就是 Java 语言的软件开发工具包，JDK中包含有JRE（Java Runtime Environment，即：Java运行环境），JRE中包括Java虚拟机（Java Virtual Machine）、Java核心类库和支持文件，而我们今天要说的主角就在Java的核心类库中。在Java的核心类库中有一个JCE（Java Cryptography Extension），JCE是一组包，它们提供用于加密、密钥生成和协商以及 Message Authentication Code（MAC）算法的框架和实现，所以这个是实现加密解密的重要类库。
>
>   在我们安装的JRE目录下有这样一个文件夹：%JAVE_HOME%\jre\lib\security（%JAVE_HOME%是自己电脑的Java路径，一版默认是：C:\Program Files\Java，具体看自己当时安装JDK和JRE时选择的路径是什么），其中包含有两个.jar文件：“local_policy.jar ”和“US_export_policy.jar”，也就是我们平时说的jar包，再通俗一点说就是Java中包含的类库（Sun公司的程序大牛封装的类库，供使用Java开发的程序员使用），这两个jar包就是我们JCE中的核心类库了。JRE中自带的“local_policy.jar ”和“US_export_policy.jar”是==支持128位密钥==的加密算法，而当我们要==使用256位密钥算法==的时候，已经超出它的范围，无法支持，所以才会报：“java.security.InvalidKeyException: Illegal key size or default parameters”的异常。那么我们怎么解决呢？
>
>   #### 解决方案
>
>   去官方下载JCE无限制权限策略文件。
>
>   Java8版本:
>
>   https://files.cnblogs.com/files/zgngg/jce_policy-8.zip
>
>   下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt
>   如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件。
>
>   如果安装了JDK，还要将两个jar文件也放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件。















### 4、API字典和相关工具

4.1、API列表
https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_7_3.shtml
我们的项目中要实现以下所有API的功能。

![image-20220824140258028](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824140258028.png)

4.2、接口规则
https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay2_0.shtml
微信支付 APIv3 使用 JSON 作为消息体的数据交换格式。

[gson使用方法](https://www.yiibai.com/gson/gson_class.html)

```java
        <!--json处理-->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
```

4.3、定义枚举
将资料文件夹中的 enums 目录复制到源码目录中。
为了开发方便，我们预先在项目中定义一些枚举。枚举中定义的内容包括==接口地址==，==支付状态==等信息。

写这个枚举类也是因为懒，没其他的原因，里面的内容就是微信支付对应功能的API，这些在微信官网全部都能找到。前面那串是固定的API地址，我们在配置文件里面写死的那个domain字段就是，所以我们把后面那串内容也弄成枚举类的话，我们使用的时候就可以直接把这两个东西拼接起来了，就不用每次都敲一遍

![image-20220824100323580](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824100323580.png)

4.4、添加工具类
将资料文件夹中的 util 目录复制到源码目录中，我们将会使用这些辅助工具简化项目的开发

![image-20220824100336911](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824100336911.png)

### 5、Native下单API

5.1、Native支付流程
https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_4.shtml

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/chapter3_1_2.png)

5.2、Native下单API
https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml

1.   2.   商户端发起支付请求，
     3.   微信端创建支付订单并生成支付二维码链接，微信端将支付二维码返回给商户端，
     4.   商户端显示支付二维码，
     5.   用户使用微信客户端扫码后发起支付。

（1）创建 WxPayController

```java
@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "网站微信支付")
@Slf4j
public class WxPayController {
}
```

（2）创建 WxPayService
接口

```java
public interface WxPayService {
}
```

实现

```java
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {
}
```

（3）定义WxPayController方法

```java
@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "网站微信支付 WxPay")
@Slf4j
public class WxPayController {
	@Resource
	private WxPayService wxPayService;
	
	@ApiOperation("调用统一下单API，生成支付二维码")
	@PostMapping("/native/{productId}")
	public AjaxResult nativePay(@PathVariable Long productId) throws Exception {
		log.info("------发起支付请求------");
		//返回支付二维码连接和订单号
		Map<String, Object> map = wxPayService.nativePay(productId);
		
		//因为AjaxResult的set返回为void,无法进行链式操作(不美观),
		//在AjaxResult上加上注解@Accessors(chain = true)解决
		/*AjaxResult ok = AjaxResult.ok();
		ok.setData(map);*/
		return AjaxResult.ok().setData(map);
	}
}
```

 AjaxResult对象中添加 `@Accessors(chain = true)`，使其可以链式操作

（4）定义WxPayService方法

>   参考：
>   API字典 -> 基础支付 -> Native支付 -> Native下单：
>   https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml
>   指引文档 -> 基础支付 -> Native支付 -> 开发指引 ->【服务端】Native下单：
>   https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_7_2.shtml

接口

```java
Map<String, Object> nativePay(Long productId) throws Exception;
```

实现

```java
	@Resource
	private WxPayConfig wxPayConfig;
	
	@Resource
	private CloseableHttpClient wxPayClient;
	
	@Override
	public Map<String, Object> nativePay(Long productId) throws IOException {
		log.info("1.生成订单");
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setTitle("test商品标题");
		orderInfo.setOrderNo(OrderNoUtils.getOrderNo()); //订单号
		orderInfo.setProductId(productId);
		orderInfo.setTotalFee(1); //分
		orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
		// TODO WU: 存入数据库
		
		log.info("2.调用统一下单API");
		HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));//避免输入一大串:https://api.mch.weixin.qq.com/v3/pay/transactions/native
		// 请求body参数
		Gson gson = new Gson();
		//Map paramsMap = new HashMap();//尽量在初始化时使用泛型
		HashMap<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("appid", wxPayConfig.getAppid());
		paramsMap.put("mchid", wxPayConfig.getMchId());
		paramsMap.put("description", orderInfo.getTitle());
		paramsMap.put("out_trade_no", orderInfo.getOrderNo());
		paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));// TODO WU: 避免输入一大串:https://7d92-115-171-63-135.ngrok.io/api/wx-pay/native/notify
		//嵌套类型,再创建一个HashMap
		//Map amountMap = new HashMap();
		HashMap<String, Object> amountMap = new HashMap<>();
		amountMap.put("total", orderInfo.getTotalFee());
		amountMap.put("currency", "CNY");
		//嵌套类型,返回到paramsMap
		paramsMap.put("amount", amountMap);
		
		//将参数转换成json字符串
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数：" + jsonParams);
		
		//设置请求头格式(JSON)
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
		
		try {
			//响应体
			String bodyAsString = EntityUtils.toString(response.getEntity());
			//响应状态码
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { //处理成功(此处状态码为微信提供,可以写死)
				log.info("成功, 返回结果 = " + bodyAsString);
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode+ ",返回结果 = " + bodyAsString);
				throw new IOException("request failed");
			}
			//响应结果
			Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
			//二维码
			String codeUrl = resultMap.get("code_url");
			
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;//返回二维码和订单编号
		} finally {
			response.close();
		}
		
	}
```

查看日志流程

![image-20220825102835531](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825102835531.png)

>   在日志中除了自己写的日志,基本没有啥有效信息,所以我们要开启Debug模式查看

5.3、签名和验签源码解析
（1）签名原理
开启debug日志

```java
# 开启debug日志
logging:
  level:
    root: debug
```

>   记得调试完后改回info

签名生成流程：
https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay4_0.shtml

日志:

在请求参数后进行**私钥签名生成**

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825110642525.png" alt="image-20220825110642525" style="zoom:50%;" />

1.   首先**构造签名串**,如果格式正确初步通过签名验证

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825104540556.png" alt="image-20220825104540556" style="zoom:50%;" />

2.   **计算签名值**:绝大多数编程语言提供的签名函数支持对*签名数据*进行签名。强烈建议商户调用该类函数，使用商户私钥对*待签名串*进行SHA256 with RSA签名，并对签名结果进行*Base64编码*得到签名值。
3.   **设置HTTP头**

![image-20220825105417001](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825105417001.png)

接下来我们了解一下商户接到微信响应时是怎样验签的

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825110801218.png" alt="image-20220825110801218" style="zoom:50%;" />

签名生成源码：

![image-20220824100932617](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824100932617.png)

（2）验签原理
签名验证流程：
https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay4_1.shtml
签名验证源码：

![image-20220824100954491](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220824100954491.png)

5.4、创建课程订单
（1）保存订单
OrderInfoService
接口：

```java
OrderInfo createOrderByProductId(Long productId);
```

实现：

```java
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
```

查找未支付订单：OrderInfoService中添加辅助方法

```java
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
```

>   `"TIMESTAMPDIFF(HOUR, create_time , now()) < 2"`//时间差SQL语句

在日志中读取SQL语句:

```sql
#查询商品ID为3的详细信息
SELECT id,title,price,create_time,update_time FROM t_product WHERE id=3 ;
#getNoPayOrderByProductId:查询未支付订单
SELECT id,title,order_no,user_id,product_id,total_fee,code_url,order_status,create_time,update_time FROM t_order_info WHERE (product_id = 3 AND order_status = '未支付' AND total_fee = 1 AND TIMESTAMPDIFF(MINUTE, create_time , now()) < 1) limit 1 ;

SELECT id,title,price,create_time,update_time FROM t_product WHERE id=3 ;
INSERT INTO t_order_info ( title, order_no, product_id, total_fee, order_status ) VALUES ( '前端课程', 'ORDER_20220825155515283', 3, 1, '未支付' ) ;
```

（2）缓存二维码
OrderInfoService
接口：

```java
void saveCodeUrl(String orderNo, String codeUrl);
```

实现：

```java
/**
* 存储订单二维码
* @param orderNo
* @param codeUrl
*/
	@Override
	public void saveCodeUrl(String orderNo, String codeUrl) {
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCodeUrl(codeUrl);
		baseMapper.update(orderInfo, queryWrapper);
	}
```

（3）修改WxPayServiceImpl 的 nativePay 方法

```java
@Resource
private OrderInfoService orderInfoService;
/**
* 创建订单，调用Native支付接口
* @param productId
* @return code_url 和 订单号
* @throws Exception
*/
@Override
public Map<String, Object> nativePay(Long productId) throws Exception {
	log.info("生成订单");
	//生成订单
	OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);
		// DO WU: 存入数据库
		String codeUrl = orderInfo.getCodeUrl();
		if(!StringUtils.isBlank(codeUrl)){
			log.info("订单(二维码)已存在");
			//返回二维码
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;//返回二维码和订单编号
		}
	log.info("调用统一下单API");
	//其他代码。。。。。。
	try {
	//其他代码。。。。。。
			//二维码
			codeUrl = resultMap.get("code_url");
			
			//保存二维码到数据库
			String orderNo = orderInfo.getOrderNo();
			orderInfoService.saveCodeUrl(orderNo,codeUrl);
	//返回二维码
	//其他代码。。。。。。
	} finally {
		response.close();
	}
}
```

5.5、显示订单列表
在我的订单页面按时间倒序显示订单列表
（1）创建OrderInfoController

```java
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
}
```

（2）定义 OrderInfoService 方法
接口

```java
List<OrderInfo> listOrderByCreateTimeDesc();
```

实现

```java
/**
* 查询订单列表，并倒序查询
* @return
*/
	@Override
	public List<OrderInfo> listOrderByCreateTimeDesc() {
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.orderByDesc(OrderInfo::getCreateTime);
		return baseMapper.selectList(queryWrapper);
	}
```

测试:

![image-20220825174938151](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825174938151.png)

### 6、支付通知API

#### 6.1、内网穿透

（1）访问ngrok官网

https://ngrok.com/
（2）注册账号、登录
（3）下载内网穿透工具
ngrok-stable-windows-amd64.zip
（4）设置你的 authToken
为本地计算机做授权配置

```shell
ngrok authtoken 6aYc6Kp7kpxVr8pY88LkG_6x9o18yMY8BASrXiDFMeS
```

（5）启动服务

```shell
ngrok http 8090
```

（6）测试外网访问

```shell
你获得的外网地址/api/test
```

#### 6.2、接收通知和返回应答

支付通知API：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_5.shtml
（1）启动ngrok

```shell
ngrok http 8090
```

（2）设置通知地址
wxpay.properties

>   注意：每次重新启动ngrok，都需要根据实际情况修改这个配置

```shell
wxpay.notify-domain=https://7d92-115-171-63-135.ngrok.io
```

（3）创建通知接口

>   通知规则：用户支付完成后，微信会把相关支付结果和用户信息==发送给商户==，商户需要接收处理
>   该消息，并返回应答。对后台通知交互时，如果微信收到商户的应答不符合规范或超时，微信认
>   为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保
>   证通知最终能成功。
>
>   （通知频率为15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m）

WxPayController

```java
/**
* 支付通知
* 微信支付通过支付通知接口将用户支付成功消息通知给商户
*/
@ApiOperation("支付通知")
@PostMapping("/native/notify")
public String nativeNotify(HttpServletRequest request, HttpServletResponse
response){
Gson gson = new Gson();
Map<String, String> map = new HashMap<>();//应答对象
//处理通知参数
String body = HttpUtils.readData(request);
Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
log.info("支付通知的id ===> {}", bodyMap.get("id"));
log.info("支付通知的完整数据 ===> {}", body);
//TODO : 签名的验证
//TODO : 处理订单
//成功应答：成功应答必须为200或204，否则就是失败应答
response.setStatus(200);
map.put("code", "SUCCESS");
map.put("message", "成功");
return gson.toJson(map);
}
```

测试:

![image-20220825201431436](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825201431436.png)

格式化:

```json
{
    "id": "30b79f4e-c179-585c-9be3-8cbe76a20009",
    "create_time": "2022-08-25T20:11:55+08:00",
    "resource_type": "encrypt-resource",
    "event_type": "TRANSACTION.SUCCESS",
    "summary": "支付成功",
    "resource": {
        "original_type": "transaction",
        "algorithm": "AEAD_AES_256_GCM",
        "ciphertext": "/lFIaOX25InUq+dCdRVcHySXGNuNU77RBnc23ZToRoUPXA/5jdVdOztK3sEfl78HyDN6vjqohgNSfs42Zuqfg5PlmBhvPB1EOItqs+eTd2sZYpUUKKzXKU/g/oJhmgg/zbaajC4loutNI4RAgyqcj4m0C9XbQ45WOgdMjYkwBdOAOQtwk/r5jF6T4r4GcSLamVCwZFD662k/hyLH1d3eVS794J6cZmTysJ2hBnfxrA0Med5Nl3QYoltzAC90TZpuXi6lKXb3eytsr2eSurcnWgduWHt7kS9kEzUOxk/jC4NA7v6hCN/Q7LoC9fCplutS871PjkGv9WQhifsHb80Ud1S556NWeABqkMMACV8J173Tta06Sr4gL27OdXluJ7oCPq6qEqN9BpEi7Iws/Yf7dJv75lcTHg4nUMP2EgHO3tpDeGOXMuQokVXwzi3+JuCb/qT+/eH1ljGPNbp6rRebGJ4r6wyDtqyGyGuvS97jtEIVnUt4bUmBCqVO5TIbkIaHtqLQw5tm5dSOagMmdGLvw0ISjuszYH3VC0qCe57pGM8a7PoMHV19hkSKd8NlAZSYaNV4DJQPWw==",
        "associated_data": "transaction",
        "nonce": "2RqS5TAVJGpC"
    }
}
```

（4）测试失败应答
用失败应答替换成功应答

```java
	/**
	 * @Description 微信支付通过支付通知接口将用户支付成功消息通知给商户
	 * @Param [request, response]
	 * @return java.lang.String
	 */
	@ApiOperation("支付通知")
	@PostMapping("/native/notify")
	public String nativeNotify(HttpServletRequest request, HttpServletResponse
			response){
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();//应答对象
		try {
            int i = 3 / 0;
			//处理通知参数
			String body = HttpUtils.readData(request);
			Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
			log.info("支付通知的id ===> {}", bodyMap.get("id"));
			log.info("支付通知的完整数据 ===> {}", body);
			//TODO : 签名的验证
			//TODO : 处理订单
			//成功应答：成功应答必须为200或204，否则就是失败应答(微信写死)
			//(新版)接收成功：HTTP应答状态码需返回200或204，无需返回应答报文。
			response.setStatus(200);
			map.put("code", "SUCCESS");
			map.put("message", "成功(可以不用返回)");
			return gson.toJson(map);
		} catch (Exception e) {
			response.setStatus(500);
			map.put("code", "ERROR");
			map.put("message", "失败(异常)");
			return gson.toJson(map);
		}
	}
```

（5）测试超时应答
回调通知注意事项：https://pay.weixin.qq.com/wiki/doc/apiv3/Practices/chapter1_1_5.shtml

>   商户系统收到支付结果通知，需要在5秒内返回应答报文，否则微信支付认为通知失败，后续会重复发送通知。

```java
// 测试超时应答：添加睡眠时间使应答超时
TimeUnit.SECONDS.sleep(5);
```

支付成功手机端:

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20220829162253.jpg" alt="微信图片_20220829162253" style="zoom:25%;" />

#### 6.3、回调验签(请求)

<strong style="color:#00b050;">新</strong> 微信支付SDK0.4.2已支持回调通知签名(不必自己编写)

[wechatpay SDK github文档](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient#%E5%9B%9E%E8%B0%83%E9%80%9A%E7%9F%A5%E7%9A%84%E9%AA%8C%E7%AD%BE%E4%B8%8E%E8%A7%A3%E5%AF%86)

![image-20220826114024857](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220826114024857.png)

[签名验证微信支付官方文档](https://pay.weixin.qq.com/wiki/doc/apiv3_partner/wechatpay/wechatpay4_1.shtml)

WxPayController

```java
	/**
	 * @Description 微信支付通过支付通知接口将用户支付成功消息通知给商户
	 * @Param [request, response]
	 * @return java.lang.String
	 */
	@ApiOperation("支付通知")
	@PostMapping("/native/notify")
	public String nativeNotify(HttpServletRequest request, HttpServletResponse
			response){
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();//应答对象
		try {
			
			String apiV3Key = wxPayConfig.getApiV3Key();
			//从微信支付回调请求头获取必要信息
			String wechatPaySerial = request.getHeader("Wechatpay-Serial");//应答平台证书序列号
			String nonce = request.getHeader("Wechatpay-Nonce");//应答随机串
			//String nonce = (String) bodyMap.get("nonce");//应答随机串(!不一致,需从请求头获取)
			String timestamp = request.getHeader("Wechatpay-Timestamp");//时间戳
			String signature = request.getHeader("Wechatpay-Signature");//签名
			
			//处理通知参数
			String body = HttpUtils.readData(request);//应答主体 [readData(request)同一request只能使用一次 ∵ br.close()]
			Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
			String notifyId = (String) bodyMap.get("id");//支付通知的id
			
			log.info("支付通知的id ===> {}", notifyId);
			log.info("支付通知的完整数据 ===> {}", body);
			log.info("_________________________");
			log.info("平台证书序列号 ===> {}", wechatPaySerial);
			log.info("nonce ===> {}", nonce);
			log.info("timestamp ===> {}", timestamp);
			log.info("signature ===> {}", signature);
			//DO : 签名的验证
			// 构建request，传入必要参数
			NotificationRequest notificationRequest = new NotificationRequest.Builder()
					.withSerialNumber(wechatPaySerial)
					.withNonce(nonce)
					.withTimestamp(timestamp)
					.withSignature(signature)
					.withBody(body)
					.build();
			NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));
			//JSON.parseObject，是将Json字符串转化为相应的对象；JSON.toJSONString则是将对象转化为Json字符串.用 Gson.toJson也行
			// 验签和解析请求体
			Notification notification = handler.parse(notificationRequest);
			/*Notification notification = wechatPay2ValidatorForRequest.notificationHandler();//旧版需要自己写*/
			
			// 从notification中获取解密报文。
			log.info("解密报文---> {}" , notification.getDecryptData());
			
			String eventType = notification.getEventType();
			if(eventType.length() == 0){
				log.error("支付回调通知验签失败");
				response.setStatus(500);
				map.put("code","ERROR");
				map.put("message","失败");
				return gson.toJson(map);
			}
			log.info("支付回调通知验签成功");
			
			//TODO : 处理订单
			
			//int i = 3 / 0;
			// 测试超时应答：添加睡眠时间使应答超时
			//TimeUnit.SECONDS.sleep(5);
			//成功应答：成功应答必须为200或204，否则就是失败应答(微信写死)
			//(新版)接收成功：HTTP应答状态码需返回200或204，无需返回应答报文。
			response.setStatus(200);
			map.put("code", "SUCCESS");
			map.put("message", "成功(可以不用返回)");
			return gson.toJson(map);
		} catch (Exception e) {
			response.setStatus(500);
			map.put("code", "ERROR");
			map.put("message", "失败(异常)");
			return gson.toJson(map);
		}
	}
```



<strong style="color:#ffc000;">旧</strong>（1）工具类
参考SDK源码中的 WechatPay2Validator 创建通知验签工具类 WechatPay2ValidatorForRequest
（2）验签

```java
@Resource
private Verifier verifier;
```

```java
//签名的验证
WechatPay2ValidatorForRequest validator
= new WechatPay2ValidatorForRequest(verifier, body, requestId);
if (!validator.validate(request)) {
log.error("通知验签失败");
//失败应答
response.setStatus(500);
map.put("code", "ERROR");
map.put("message", "通知验签失败");
return gson.toJson(map);
}
log.info("通知验签成功");
//TODO : 处理订单
```

#### 6.4、解密

![image-20220825184657499](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825184657499.png)

<strong style="color:#00b050;">新</strong> 由`notification.getDecryptData()`从日志获取解密报文

[支付成功通知参数](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_5.shtml#%E6%94%AF%E4%BB%98%E6%88%90%E5%8A%9F%E9%80%9A%E7%9F%A5%E5%8F%82%E6%95%B0)

```json
{
    "mchid": "1558950191",//商户号
    "appid": "wx74862e0dfcf69954",//应用ID
    "out_trade_no": "ORDER_20220826142033607",//商户订单号
    "transaction_id": "4200001575202208266869498316",//微信支付订单号
    "trade_type": "NATIVE",//交易类型
    "trade_state": "SUCCESS",//交易状态
    "trade_state_desc": "支付成功",//交易状态描述
    "bank_type": "OTHERS",//付款银行
    "attach": "",//附加数据
    "success_time": "2022-08-26T14:20:40+08:00",//支付完成时间
    "payer": {//-支付者
        "openid": "oHwsHuOo4QhLkxeCtZDe3rTN9_T4"//用户标识
    },
    "amount": {//-订单金额
        "total": 1,//总金额(分)
        "payer_total": 1,//用户支付金额
        "currency": "CNY",//货币类型
        "payer_currency": "CNY"//用户支付币种
    }
}
```



<strong style="color:#ffc000;">旧</strong> ![image-20220825184709223](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825184709223.png)

![image-20220825184719433](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825184719433.png)

（1）WxPayController
nativeNotify 方法中添加处理订单的代码

```java
//处理订单
wxPayService.processOrder(bodyMap);
```

（1）WxPayService
接口：

```java
void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;
```

实现：

```java
@Override
public void processOrder(Map<String, Object> bodyMap) throws
GeneralSecurityException {
log.info("处理订单");
String plainText = decryptFromResource(bodyMap);
//转换明文
//更新订单状态
//记录支付日志
}
```

辅助方法：

```java
/**
* 对称解密
* @param bodyMap
* @return
*/
private String decryptFromResource(Map<String, Object> bodyMap) throws
GeneralSecurityException {
log.info("密文解密");
//通知数据
Map<String, String> resourceMap = (Map) bodyMap.get("resource");
//数据密文
String ciphertext = resourceMap.get("ciphertext");
//随机串
String nonce = resourceMap.get("nonce");
//附加数据
String associatedData = resourceMap.get("associated_data");
    log.info("密文 ===> {}", ciphertext);
AesUtil aesUtil = new
AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
String plainText =
aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
nonce.getBytes(StandardCharsets.UTF_8),
ciphertext);
log.info("明文 ===> {}", plainText);
return plainText;
}
```

#### 6.5、处理订单状态和记录支付日志

（1）在完善processOrder方法

```java
@Resource
private PaymentInfoService paymentInfoService;
@Override
public void processOrder(Map<String, Object> bodyMap) throws
GeneralSecurityException {
log.info("处理订单");
String plainText = decryptFromResource(bodyMap);
//转换明文
Gson gson = new Gson();
Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
String orderNo = (String)plainTextMap.get("out_trade_no");
//更新订单状态
orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
//记录支付日志
paymentInfoService.createPaymentInfo(plainText);
}
```

（2）更新订单状态
OrderInfoService
接口：

```java
void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);
```

实现：

```java
/**
* 根据订单编号更新订单状态
* @param orderNo
* @param orderStatus
*/
	@Override
	public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
		log.info("更新订单状态 ===> {}", orderStatus.getType());
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderNo,orderNo);
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderStatus(orderStatus.getType());
		baseMapper.update(orderInfo, queryWrapper);
	}
```

（3）处理支付日志
PaymentInfoService
接口：

```java
void createPaymentInfo(String plainText);
```

实现：

```java
/**
* 记录支付日志
* @param plainText
*/
	@Override
	public void createPaymentInfo(String plainText) {
		log.info("记录支付日志");
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
		//商户订单号
		String orderNo = (String)plainTextMap.get("out_trade_no");
		//微信支付订单号
		String transactionId = (String)plainTextMap.get("transaction_id");
		//交易类型
		String tradeType = (String)plainTextMap.get("trade_type");
		//交易状态
		String tradeState = (String)plainTextMap.get("trade_state");
		log.info("交易状态---> {}" , tradeState);
		//-订单金额
		Map<String, Object> amount = (Map)plainTextMap.get("amount");
		//坑:不能直接转为int(Gson缺陷) || 实际中应转换为BigDecimal(不要转为Double,会有精度损失[此处支付金额单位为(分)应该没事?])
		//用户实际支付金额
		//Integer payerTotal = ((BigDecimal) amount.get("payer_total")).intValue();//报错
		Integer payerTotal = ((Double) amount.get("payer_total")).intValue();
		log.info("订单实际金额---> {}" , payerTotal);
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setOrderNo(orderNo);
		paymentInfo.setPaymentType(PayType.WXPAY.getType());
		paymentInfo.setTransactionId(transactionId);
		paymentInfo.setTradeType(tradeType);
		paymentInfo.setTradeState(tradeState);
		paymentInfo.setPayerTotal(payerTotal);
		//备份解密报文(各支付厂商可能返回类型不同,以备不时之需)
		paymentInfo.setContent(plainText);
		baseMapper.insert(paymentInfo);
	}
```

#### 6.6、处理重复通知

（1）测试重复的通知

![image-20220825185203689](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825185203689.png)

会影响支付日志重复响应

![image-20220826165401472](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220826165401472.png)

（2）处理重复通知

```java
			//DO : 处理订单:通过解密报文
			wxPayService.processOrder(plainText);
			
			//应答超时
			//设置响应超时，可以接收到微信支付的重复的支付结果通知。
			//通知重复，数据库会记录多余的支付日志
			TimeUnit.SECONDS.sleep(5);
```

在 processOrder 方法中，更新订单状态之前，添加如下代码

```java
		//处理重复通知
		//保证接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
		String orderStatus = orderInfoService.getOrderStatus(orderNo);
		if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
			return;
		}
		
		//更新订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
		//记录支付日志
		paymentInfoService.createPaymentInfo(plainText);
```

OrderInfoService
接口：

```java
String getOrderStatus(String orderNo);
```

实现：

```java
/**
* 根据订单号获取订单状态
* @param orderNo
* @return
*/
	@Override
	public String getOrderStatus(String orderNo) {
		QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("order_no", orderNo);
		OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
		//防止被删除的订单的回调通知的调用
		if(orderInfo == null){
			return null;
		}
		return orderInfo.getOrderStatus();
	}
```

#### 6.7、数据锁

![image-20220825185338957](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220825185338957.png)

（1）测试通知并发

```java
		//模拟通知并发
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//更新订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
		//记录支付日志
		paymentInfoService.createPaymentInfo(plainText);
```

（2）定义ReentrantLock
定义 ReentrantLock 进行并发控制。注意，必须手动释放锁。

>   也可用synchronize锁,分布式情况则用Redis分布式锁

```java
	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public void processOrder(String plainText) {
		log.info("处理订单");
		
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
		//商户订单号
		String orderNo = (String) plainTextMap.get("out_trade_no");
		
		/*在对业务数据进行状态检查和处理之前，
		要采用数据锁进行并发控制，
		以避免函数重入造成的数据混乱*/
		//尝试获取锁：
		// 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
		if(lock.tryLock()){
			try {
				//处理重复通知
				//保证接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
					return;
				}
				
				//模拟通知并发
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
				//记录支付日志
				paymentInfoService.createPaymentInfo(plainText);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
```

### 7、商户定时查询本地订单

#### 7.1、后端定义商户查单接口

支付成功后，商户侧查询本地数据库，订单是否支付成功

```java
/**
* 查询本地订单状态
*/
	@ApiOperation("查询本地订单状态")
	@GetMapping("/query-order-status/{orderNo}")
	public AjaxResult queryOrderStatus(@PathVariable String orderNo) {
		String orderStatus = orderInfoService.getOrderStatus(orderNo);
		if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {//支付成功
			return AjaxResult.ok();
		}
		return AjaxResult.ok().setCode(101).setMessage("支付中...");
	}
```

7.2、前端定时轮询查单
在二维码展示页面，前端定时轮询查询订单是否已支付，如果支付成功则跳转到订单页面
（1）定义定时器

```java
//启动定时器
this.timer = setInterval(() => {
	//查询订单是否支付成功
	this.queryOrderStatus()
}, 3000)
```

（2）查询订单

```java
	@ApiOperation("查询本地订单状态")
	@GetMapping("/query-order-status/{orderNo}")
	public AjaxResult queryOrderStatus(@PathVariable String orderNo) {
		String orderStatus = orderInfoService.getOrderStatus(orderNo);
		if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {//支付成功
			return AjaxResult.ok();
		}
		return AjaxResult.ok().setCode(101).setMessage("支付中...");
	}
```

前端控制台:

![image-20220826175236737](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220826175236737.png)

### 8、用户取消订单API

实现用户主动取消订单的功能

#### 8.1、定义取消订单接口

```java
/**
* 用户取消订单
* @param orderNo
* @return
* @throws Exception
*/
	@ApiOperation("用户取消订单")
	@PostMapping("/cancel/{orderNo}")
	public AjaxResult cancel(@PathVariable String orderNo) throws Exception {
		log.info("取消订单");
		wxPayService.cancelOrder(orderNo);
		return AjaxResult.ok().setMessage("订单已取消");
	}
```

8.2、WxPayService
接口

```java
void cancelOrder(String orderNo) throws Exception;
```

实现

```java
/**
* 用户取消订单
* @param orderNo
*/
	@Override
	public void cancelOrder(String orderNo) throws Exception {
		//调用微信支付的关单接口
		this.closeOrder(orderNo);
		//更新商户端的订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
	}
```

关单方法

```java
	/**
	 * @Description 关单接口的调用
	 * @Param [orderNo]
	 * @return void
	 */
	private void closeOrder(String orderNo) throws Exception {
		log.info("关单接口的调用，订单号 ===> {}", orderNo);
		//创建远程请求对象
		//String.format()双参数,第二个参数可替换第一个参数中的占位符
		String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
		url = wxPayConfig.getDomain().concat(url);
		HttpPost httpPost = new HttpPost(url);
		
		Gson gson = new Gson();
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("mchid", wxPayConfig.getMchId());
	//-| 开关单此处代码一致
		//将参数转换成json字符串
		//组装json请求体
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数 ===> {}", jsonParams);
		
		//设置请求头格式(JSON)
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		
		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
	//-|
		try {
			int statusCode = response.getStatusLine().getStatusCode();//响应状态码
			if (statusCode == 200) { //处理成功
				log.info("成功200");
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功204");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode);
				throw new IOException("request failed");
			}
		} finally {
			response.close();
		}
	}
```

### 9、微信支付查单API

#### 9.1、查单接口的调用

商户后台未收到异步支付结果通知时，商户应该主动调用《微信支付查单接口》，同步订单状态。
（1）WxPayController

```java
	/**
	 * @Description 查询订单
	 * @Param [orderNo]
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("查询订单(测试订单状态用)")
	@GetMapping("/query/{orderNo}")
	public AjaxResult queryOrder(@PathVariable String orderNo) throws Exception {
		log.info("查询订单");
		String bodyAsString = wxPayService.queryOrder(orderNo);
		return AjaxResult.ok().setMessage("查询成功").data("bodyAsString", bodyAsString);
	}
```

（2）WxPayService
接口

```java
String queryOrder(String orderNo) throws Exception;
```

由请求示例可知

![image-20220827112900640](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220827112900640.png)

实现

```java
/**
* 查单接口调用
*/
	@Override
	public String queryOrder(String orderNo) throws IOException {
		log.info("查单接口调用 ===> {}", orderNo);
		String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
		url = wxPayConfig.getDomain().concat(url).concat("?mchid=").concat(wxPayConfig.getMchId());
		HttpGet httpGet = new HttpGet(url);
		//区别于POST方式,只需设置请求头格式(JSON)
		httpGet.setHeader("Accept", "application/json");
		//完成签名并执行请求(与创建订单，调用Native支付接口相似)
		// TODO WU: 考虑合并重复代码为方法
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
			int statusCode = response.getStatusLine().getStatusCode();//响应状态码
			if (statusCode == 200) { //处理成功
				log.info("成功, 返回结果 = " + bodyAsString);
			} else if (statusCode == 204) { //处理成功，无返回Body
				log.info("成功");
			} else {
				log.info("Native下单失败,响应码 = " + statusCode+ ",返回结果 = " +
						bodyAsString);
				throw new IOException("request failed");
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
```

#### 9.2、集成Spring Task

Spring 3.0后提供Spring Task实现任务调度
（1）启动类添加注解
statistics启动类添加注解

```java
@EnableScheduling	//任务调度【开启调度】
```

（2）测试定时任务
创建 task 包，创建 WxPayTask.java

```java
@Slf4j
@Component
public class WxPayTask {
	/**
	 * 测试
	 * (cron="秒 分 时 日 月 周")
	 * *：每隔一秒执行
	 * 0/3：从第0秒开始，每隔3秒执行一次
	 * 1-3: 从第1秒开始执行，到第3秒结束执行
	 * 1,2,3：第1、2、3秒执行
	 * ?：不指定，若指定日期，则不指定周，反之同理
	 */
	@Scheduled(cron="0/3 * * * * ?")
	public void task1() {
		log.info("task1 执行");
	}
}
```

#### 9.3、定时查找超时订单

（1）WxPayTask

```java
@Resource
private OrderInfoService orderInfoService;
@Resource
private WxPayService wxPayService;
/**
* 从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
*/
@Scheduled(cron = "0/30 * * * * ?")
public void orderConfirm() throws Exception {
log.info("orderConfirm 被执行......");
List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(5);
for (OrderInfo orderInfo : orderInfoList) {
String orderNo = orderInfo.getOrderNo();
log.warn("超时订单 ===> {}", orderNo);
    //核实订单状态：调用微信支付查单接口
wxPayService.checkOrderStatus(orderNo);
}
}
```

（2）OrderInfoService
接口

```java
List<OrderInfo> getNoPayOrderByDuration(int minutes);
```

实现

```java
/**
* 找出创建超过minutes分钟并且未支付的订单
* @param minutes
* @return
*/
	@Override
	public List<OrderInfo> getNoPayOrderByDuration(int minutes) {
		/*//minutes分钟之前的时间(另解)
		Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
		QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.le("create_time", instant);*/
		
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderStatus,OrderStatus.NOTPAY.getType())
					//判断订单创建时间是否大于n分钟(定时调用查询订单API)<查找n分钟外未支付的订单>
					.apply("TIMESTAMPDIFF(MINUTE, create_time , now()) > " + minutes);
		return baseMapper.selectList(queryWrapper);
	}
```

日志输出:

![image-20220827153734356](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220827153734356.png)

#### 9.4、处理超时订单

WxPayService
核实订单状态
接口：

```java
void checkOrderStatus(String orderNo) throws Exception;
```

实现：

```java
/**
* 根据订单号查询微信支付查单接口，核实订单状态
* 如果订单已支付，则更新商户端订单状态，并记录支付日志
* 如果订单未支付，则调用关单接口关闭订单，并更新商户端订单状态
* @param orderNo
*/
	@Override
	public void checkOrderStatus(String orderNo) throws Exception {
		log.warn("根据订单号核实订单状态 ===> {}", orderNo);
		//调用微信支付查单接口
		String result = this.queryOrder(orderNo);
		Gson gson = new Gson();
		Map resultMap = gson.fromJson(result, HashMap.class);
		//获取微信支付端的订单状态
		Object tradeState = resultMap.get("trade_state");
		//判断订单状态
		if(WxTradeState.SUCCESS.getType().equals(tradeState)){
			log.warn("核实订单已支付 ===> {}", orderNo);
			//如果确认订单已支付则更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
			//记录支付日志
			paymentInfoService.createPaymentInfo(result);
		}
		if(WxTradeState.NOTPAY.getType().equals(tradeState)){
			log.warn("核实订单未支付 ===> {}", orderNo);
			//如果订单未支付，则调用关单接口
			this.closeOrder(orderNo);
			//更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
	}
```

![image-20220827164205290](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220827164205290.png)

### 11、申请退款API

文档：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_9.shtml

状态机:

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/chapter24_1.png)

#### 11.1、创建退款单

（1）根据订单号查询订单
OrderInfoService
接口：

```java
OrderInfo getOrderByOrderNo(String orderNo);
```

实现：

```java
	@Override
	public OrderInfo getOrderByOrderNo(String orderNo) {
		LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(OrderInfo::getOrderNo, orderNo)
					.last(" limit 1");
		return baseMapper.selectOne(queryWrapper);
	}
```

（2）创建退款单记录
RefundsInfoService
接口：

```java
RefundInfo createRefundByOrderNo(String orderNo, String reason);
```

实现:

```java
	@Override
	public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
		//根据订单号获取订单信息
		OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
		//根据订单号生成退款订单
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setOrderNo(orderNo);//订单编号
		refundInfo.setRefundNo(OrderNoUtils.getRefundNo());//退款单编号
		refundInfo.setTotalFee(orderInfo.getTotalFee());//原订单金额(分)
		refundInfo.setRefund(orderInfo.getTotalFee());//退款金额(分)[全额退款]
		refundInfo.setReason(reason);//退款原因
		//保存退款订单
		baseMapper.insert(refundInfo);
		return refundInfo;
	}
```

#### 11.2、更新退款单

RefundInfoService
接口：

```java
void updateRefund(String bodyAsString);
```

实现：

```java
	@Override
	public void updateRefund(String content) {
		//将json字符串转换成Map
		Gson gson = new Gson();
		Map<String, String> resultMap = gson.fromJson(content, HashMap.class);
		//根据退款单编号修改退款单
		LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(RefundInfo::getRefundNo, resultMap.get("out_refund_no"));
		//设置要修改的字段
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setRefundId(resultMap.get("refund_id"));//微信支付退款单号
		//查询退款和申请退款中的返回参数
		if(resultMap.get("status") != null){
			refundInfo.setRefundStatus(resultMap.get("status"));//退款状态
			refundInfo.setContentReturn(content);//将全部响应结果存入数据库的content字段
		}
		//退款回调中的回调参数
		if(resultMap.get("refund_status") != null){
			refundInfo.setRefundStatus(resultMap.get("refund_status"));//退款状态
			refundInfo.setContentNotify(content);//将全部响应结果存入数据库的content字段
		}
		//更新退款单
		baseMapper.update(refundInfo, queryWrapper);
	}
```

#### 11.3、申请退款

（1）WxPayController

```java
	/**
	 * @Description 申请退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("申请退款")
	@PostMapping("/refunds/{orderNo}/{reason}")
	public AjaxResult refunds(@PathVariable String orderNo, @PathVariable String reason)
			throws Exception {
		log.info("申请退款");
		wxPayService.refund(orderNo, reason);
		return AjaxResult.ok();
	}
```

（2）WxPayService
接口：

```java
void refund(String orderNo, String reason) throws Exception;
```

实现：

>   退款需保持事务一致性

```java
	@Resource
	private RefundInfoService refundsInfoService;

	/**
	 * @Description 退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void refund(String orderNo, String reason) throws IOException {
		log.info("创建退款单记录");
		//根据订单编号创建退款单
		RefundInfo refundsInfo = refundsInfoService.createRefundByOrderNo(orderNo, reason);
		log.info("调用退款API");
		//调用统一下单API
		String url = wxPayConfig.getDomain().concat(WxApiType.DOMESTIC_REFUNDS.getType());
		HttpPost httpPost = new HttpPost(url);
		// 请求body参数
		Gson gson = new Gson();
		Map paramsMap = new HashMap();
		paramsMap.put("out_trade_no", orderNo);//订单编号
		paramsMap.put("out_refund_no", refundsInfo.getRefundNo());//退款单编号
		paramsMap.put("reason",reason);//退款原因
		paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));//退款通知地址
		//-| 金额
		Map amountMap = new HashMap();
		amountMap.put("refund", refundsInfo.getRefund());//退款金额
		amountMap.put("total", refundsInfo.getTotalFee());//原订单金额
		amountMap.put("currency", "CNY");//退款币种
		paramsMap.put("amount", amountMap);
		//将参数转换成json字符串
		String jsonParams = gson.toJson(paramsMap);
		log.info("请求参数 ===> {}" + jsonParams);
		StringEntity entity = new StringEntity(jsonParams,"utf-8");
		entity.setContentType("application/json");//设置请求报文格式
		httpPost.setEntity(entity);//将请求报文放入请求对象
		httpPost.setHeader("Accept", "application/json");//设置响应报文格式
		//完成签名并执行请求，并完成验签
		CloseableHttpResponse response = wxPayClient.execute(httpPost);
		try {
			//解析响应结果
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 退款返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("退款异常, 响应码 = " + statusCode+ ", 退款返回结果 = " + bodyAsString);
			}
			//更新订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_PROCESSING);
			//更新退款单
			refundsInfoService.updateRefund(bodyAsString);
		} finally {
			response.close();
		}
	}
```

### 12、查询退款API

文档：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_10.shtml
#### 12.1、查单接口的调用

（1）WxPayController

```java
	/**
	 * @Description 查询退款：测试用
	 * @Param [refundNo] 退款单号
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("查询退款：测试用")
	@GetMapping("/query-refund/{refundNo}")
	public AjaxResult queryRefund(@PathVariable String refundNo) throws Exception {
		log.info("查询退款");
		String result = wxPayService.queryRefund(refundNo);
		return AjaxResult.ok().setMessage("查询成功").data("result", result);
	}
```

（2）WxPayService
接口：

```java
String queryRefund(String orderNo) throws Exception;
```

实现：

```java
	@Override
	public String queryRefund(String refundNo) throws IOException {
		log.info("查询退款接口调用 ===> {}", refundNo);
		String url = String.format(WxApiType.DOMESTIC_REFUNDS_QUERY.getType(), refundNo);
		url = wxPayConfig.getDomain().concat(url);
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept", "application/json");
		//完成签名并执行请求
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 查询退款返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("查询退款异常, 响应码 = " + statusCode+ ",查询退款返回结果 = " + bodyAsString);
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
```

微信支付返回JSON

```json
{
    "code": 200,
    "message": "查询成功",
    "data": {
        "result": "{\"amount\":{\"currency\":\"CNY\",\"discount_refund\":0,\"from\":[],\"payer_refund\":1,\"payer_total\":1,\"refund\":1,\"settlement_refund\":1,\"settlement_total\":1,\"total\":1},\"channel\":\"BALANCE\",\"create_time\":\"2022-08-27T17:55:53+08:00\",\"funds_account\":\"AVAILABLE\",\"out_refund_no\":\"REFUND_20220827175554920\",\"out_trade_no\":\"ORDER_20220827100531879\",\"promotion_detail\":[],\"refund_id\":\"50302303112022082724253890310\",\"status\":\"SUCCESS\",\"success_time\":\"2022-08-27T17:56:11+08:00\",\"transaction_id\":\"4200001542202208276696621997\",\"user_received_account\":\"支付用户零钱\"}"
    }
}
```

#### 12.2、定时查找退款中的订单

（1）WxPayTask

```java
	@Resource
	private RefundInfoService refundInfoService;

	/**
	 * @Description 从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未成功的退款单
	 * @Param []
	 * @return void
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void refundConfirm() throws Exception {
		log.info("refundConfirm 被执行......");
		//找出申请退款超过5分钟并且未成功的退款单
		List<RefundInfo> refundInfoList = refundInfoService.getNoRefundOrderByDuration(5);
		for (RefundInfo refundInfo : refundInfoList) {
			String refundNo = refundInfo.getRefundNo();
			log.warn("超时未退款的退款单号 ===> {}", refundNo);
		//核实订单状态：调用微信支付查询退款接口
			wxPayService.checkRefundStatus(refundNo);
		}
	}
```

（2）RefundInfoService
接口


```java
List<RefundInfo> getNoRefundOrderByDuration(int minutes);
```


实现

```java
	@Override
	public List<RefundInfo> getNoRefundOrderByDuration(int minutes) {
		LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
		
		queryWrapper.eq(RefundInfo::getRefundStatus, WxRefundStatus.PROCESSING.getType())
					//判断退款单创建时间是否大于n分钟(定时调用查询订单API)<查找n分钟外未支付的订单>
					.apply("TIMESTAMPDIFF(MINUTE, create_time , now()) > " + minutes);
		return baseMapper.selectList(queryWrapper);
	}
```


#### 12.3、处理超时未退款订单

WxPayService
核实订单状态
接口：

```java
void checkRefundStatus(String refundNo);
```

实现:

```java
	@Override
	public void checkRefundStatus(String refundNo) throws IOException {
		log.warn("根据退款单号核实退款单状态 ===> {}", refundNo);
		//调用查询退款单接口
		String result = this.queryRefund(refundNo);
		//组装json请求体字符串
		Gson gson = new Gson();
		Map<String,String> resultMap = gson.fromJson(result, HashMap.class);
		//获取微信支付端退款状态
		//Object tradeState = resultMap.get("trade_state");
		String status = resultMap.get("status");//退款状态
		String orderNo = resultMap.get("out_trade_no");//订单号
		
		//判断退款单状态
		if(WxRefundStatus.SUCCESS.getType().equals(status)){
			log.warn("核实订单已退款成功 ===> {}", refundNo);
			//如果确认退款成功，则更新**订单状态**
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
			//更新退款单
			refundsInfoService.updateRefund(result);
		}
		if(WxRefundStatus.ABNORMAL.getType().equals(status)){
			log.warn("核实订单退款异常 ===> {}", refundNo);
			//如果确认退款异常，则更新**订单状态**
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_ABNORMAL);
			//更新退款单
			refundsInfoService.updateRefund(result);
		}
		
	}
```

### 13、退款结果通知API

文档：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_11.shtml

#### 13.1、接收退款通知

WxPayController

```java
	@ApiOperation("退款结果通知")
	@PostMapping("/refunds/notify")//Postman单发无用
	public String refundsNotify(HttpServletRequest request, HttpServletResponse
			response){
		log.info("退款通知执行");
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();//应答对象
		try {
			
			String apiV3Key = wxPayConfig.getApiV3Key();
			//从微信支付回调请求头获取必要信息
			String wechatPaySerial = request.getHeader("Wechatpay-Serial");//应答平台证书序列号
			String nonce = request.getHeader("Wechatpay-Nonce");//应答随机串
			String timestamp = request.getHeader("Wechatpay-Timestamp");//时间戳
			String signature = request.getHeader("Wechatpay-Signature");//签名
			
			//处理通知参数
			String body = HttpUtils.readData(request);//应答主体 [readData(request)同一request只能使用一次 ∵ br.close()]
			Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
			String notifyId = (String) bodyMap.get("id");//退款通知的id
			
			log.info("退款通知的id ===> {}", notifyId);
			log.info("退款通知的完整数据 ===> {}", body);
			log.info("_________________________");
			log.info("平台证书序列号 ===> {}", wechatPaySerial);
			log.info("nonce ===> {}", nonce);
			log.info("timestamp ===> {}", timestamp);
			log.info("signature ===> {}", signature);
			//DO : 签名的验证
			// 构建request，传入必要参数
			NotificationRequest notificationRequest = new NotificationRequest.Builder()
					.withSerialNumber(wechatPaySerial)
					.withNonce(nonce)
					.withTimestamp(timestamp)
					.withSignature(signature)
					.withBody(body)
					.build();
			NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));

			// 验签和解析请求体
			Notification notification = handler.parse(notificationRequest);
			
			// 从notification中获取解密报文。
			String plainText = notification.getDecryptData();
			log.info("解密报文---> {}" , plainText);
			
			String eventType = notification.getEventType();
			if(eventType.length() == 0){
				log.error("支付回调通知验签失败");
				response.setStatus(500);
				map.put("code","ERROR");
				map.put("message","失败");
				return gson.toJson(map);
			}
			log.info("支付回调通知验签成功");
			
			//DO : 处理退款单:通过解密报文<**和支付结果通知的唯一区别**>
			wxPayService.processRefund(plainText);
			
			//成功应答：成功应答必须为200或204，否则就是失败应答(微信写死)
			//(新版)接收成功：HTTP应答状态码需返回200或204，无需返回应答报文。
			response.setStatus(200);
			map.put("code", "SUCCESS");
			map.put("message", "成功(可以不用返回)");
			return gson.toJson(map);
		} catch (Exception e) {
			response.setStatus(500);
			map.put("code", "ERROR");
			map.put("message", "失败(异常)");
			return gson.toJson(map);
		}
	}
```

#### 13.2、处理订单和退款单

WxPayService
接口：

```java
void processRefund(Map<String, Object> bodyMap) throws Exception;
```

实现：

```java
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void processRefund(String plainText) {
		log.info("退款单");
		
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
		//商户订单号
		String orderNo = (String) plainTextMap.get("out_trade_no");
		
		/*在对业务数据进行状态检查和处理之前，
		要采用数据锁进行并发控制，
		以避免函数重入造成的数据混乱*/
		//尝试获取锁：
		// 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
		if(lock.tryLock()){
			try {
				//处理重复通知
				//保证接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				//<**与处理订单的区别**>
				if (!OrderStatus.REFUND_PROCESSING.getType().equals(orderStatus)) {
					return;
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
				/*//记录支付日志
				paymentInfoService.createPaymentInfo(plainText);*/
				//更新退款单<**与处理订单的区别**>
				refundsInfoService.updateRefund(plainText);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
```

### 14、账单

#### 14.1、申请交易账单和资金账单

（1）WxPayController

```java
	@ApiOperation("获取账单url：测试用")
	@GetMapping("/querybill/{billDate}/{type}")
	public AjaxResult queryTradeBill(@PathVariable String billDate, @PathVariable String type) throws Exception {
		log.info("获取账单url");
		String downloadUrl = wxPayService.queryBill(billDate, type);
		return AjaxResult.ok().setMessage("获取账单url成功").data("downloadUrl", downloadUrl);
	}
```

（2）WxPayService
接口：

```java
String queryBill(String billDate, String type) throws IOException;
```

实现:

```java
	/**
	 * @Description 查询账单
	 * @Param [billDate] 订单日期(无法查询当日账单) 格式:2022-08-26
	 * @Param [type] 查询账单类型 选择:{交易账单 tradebill,资金账单 fundflowbill}
	 * @return java.lang.String
	 */
	@Override
	public String queryBill(String billDate, String type) throws IOException {
		log.warn("申请账单接口调用,日期: {}", billDate);
		String url;//不用初始化
		//微信支付提供,写死了
		if("tradebill".equals(type)){
			url = WxApiType.TRADE_BILLS.getType();
		}else if("fundflowbill".equals(type)){
			url = WxApiType.FUND_FLOW_BILLS.getType();
		}else{
			throw new RuntimeException("不支持的账单类型");
		}
		
		url = wxPayConfig.getDomain().concat(url).concat("?bill_date=").concat(billDate);
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept", "application/json");
		
		//使用wxPayClient发送请求得到响应
		CloseableHttpResponse response = wxPayClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 申请账单返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("申请账单异常, 响应码 = " + statusCode+ ",申请账单返回结果 = " + bodyAsString);
			}
			
			//获取账单下载地址
			Gson gson = new Gson();
			Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
			return resultMap.get("download_url");
		} finally {
			response.close();
		}
	}
```

测试:

![image-20220829145013887](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220829145013887.png)

返回的url需使用api方式下载

![image-20220829144926603](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220829144926603.png)

#### 14.2、下载账单

（1）WxPayController

```java
	@ApiOperation("下载账单")
	@GetMapping("/downloadbill/{billDate}/{type}")
	public AjaxResult downloadBill(@PathVariable String billDate, @PathVariable String type) throws Exception {
		log.info("下载账单");
		String result = wxPayService.downloadBill(billDate, type);
		return AjaxResult.ok().data("result", result);
	}
```

（2）WxPayService
接口：

```java
String downloadBill(String billDate, String type) throws IOException;
```

实现：

```java
	@Resource
	private CloseableHttpClient wxPayNoSignClient; //无需应答签名
	
	@Override
	public String downloadBill(String billDate, String type) throws IOException {
		log.warn("下载账单接口调用,日期: {},账单类型: {}", billDate, type);
		//获取账单url地址
		String downloadUrl = this.queryBill(billDate, type);
		
		//创建远程Get 请求对象
		HttpGet httpGet = new HttpGet(downloadUrl);
		httpGet.addHeader("Accept", "application/json");
		//使用wxPayClient发送请求得到响应
		CloseableHttpResponse response = wxPayNoSignClient.execute(httpGet);
		try {
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				log.info("成功, 下载账单返回结果 = " + bodyAsString);
			} else if (statusCode == 204) {
				log.info("成功");
			} else {
				throw new RuntimeException("下载账单异常, 响应码 = " + statusCode+ ",下载账单返回结果 = " + bodyAsString);
			}
			return bodyAsString;
		} finally {
			response.close();
		}
	}
```

无需签名,会报错,需另创一个wxPayNoSignClient对象 这个需要在WxPayConfig中手动添加到@Bean容器

```java
	@Bean(name = "wxPayNoSignClient")
	public CloseableHttpClient getWxPayNoSignClient(){
		log.info("初始化wxPayNoSignClient");
		//获取商户私钥
		PrivateKey privateKey = getPrivateKey(privateKeyPath);
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(mchId, mchSerialNo, privateKey)
				//设置响应对象无需签名<与上方区别>
				//直接用lamdba的方式定义了接口的实现，return了方法的返回值为 ture，大致意思就是所有的响应验签结果都是true
				//具体原因就是 下载账单请求的响应里，在head中 没有 `WECHAT_PAY_SERIAL` ，所以使用原来的验签器时在方法里 get不到值报的错，导致验签不通过
				.withValidator(response -> true);
		CloseableHttpClient wxPayNoSignClient = builder.build();
		log.info("wxPayNoSignClient初始化完成");
		return wxPayNoSignClient;
	}
```

![image-20220829154432787](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220829154432787.png)

测试:

![image-20220829163201919](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220829163201919.png)

可以看到其实我们返回的是字符串,真正下载是在前端进行的:

payment-demo-front\src\views

```vue
<template>
  <div class="bg-fa of">
    <section id="index" class="container">
      <header class="comm-title">
        <h2 class="fl tac">
          <span class="c-333">账单申请</span>
        </h2>
      </header>
      
      <el-form :inline="true" >
        <el-form-item>
            <el-date-picker v-model="billDate" value-format="yyyy-MM-dd" placeholder="选择账单日期" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="downloadBill('tradebill')">下载交易账单</el-button>
        </el-form-item>
         <el-form-item>
            <el-button type="primary" @click="downloadBill('fundflowbill')">下载资金账单</el-button>
        </el-form-item>
      </el-form>
    </section>

  </div>
</template>

<script>
import billApi from '../api/bill'

export default {
  data () {
    return {
       billDate: '' //账单日期
    }
  },

  methods: {

    //下载账单
    downloadBill(type){
      //获取账单内容
      billApi.downloadBill(this.billDate, type).then(response => {
        console.log(response)
        const element = document.createElement('a')
        element.setAttribute('href', 'data:application/vnd.ms-excel;charset=utf-8,' + encodeURIComponent(response.data.result))
        element.setAttribute('download', this.billDate + '-' + type)
        element.style.display = 'none'
        element.click()
      })
    }
  }
}
</script>
```



## 五、基础支付API V2

### 1、[V2和V3的比较](https://pay.weixin.qq.com/wiki/doc/apiv3/index.shtml)

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220829170815782.png" alt="image-20220829170815782" style="zoom:50%;" />

### 2、引入依赖和工具

#### 2.1、引入依赖

```xml
        <!--微信支付V2-->
        <dependency>
            <groupId>com.github.wxpay</groupId>
            <artifactId>wxpay-sdk</artifactId>
            <version>0.0.3</version>
        </dependency>
```

#### 2.2、复制工具类

HttpClientUtils

```java
package com.hsxy.paymentdemo.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @name HttpClientUtils
 * @Description http请求客户端(微信支付V2使用)
 * @author WU
 * @Date 2022/8/29 17:20
 */
public class HttpClientUtils {
	private String url;
	private Map<String, String> param;
	private int statusCode;
	private String content;
	private String xmlParam;
	private boolean isHttps;
	
	public boolean isHttps() {
		return isHttps;
	}
	
	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}
	
	public String getXmlParam() {
		return xmlParam;
	}
	
	public void setXmlParam(String xmlParam) {
		this.xmlParam = xmlParam;
	}
	
	public HttpClientUtils(String url, Map<String, String> param) {
		this.url = url;
		this.param = param;
	}
	
	public HttpClientUtils(String url) {
		this.url = url;
	}
	
	public void setParameter(Map<String, String> map) {
		param = map;
	}
	
	public void addParameter(String key, String value) {
		if (param == null) {
			param = new HashMap<String, String>();
		}
		param.put(key, value);
	}
	
	public void post() throws ClientProtocolException, IOException {
		HttpPost http = new HttpPost(url);
		setEntity(http);
		execute(http);
	}
	
	public void put() throws ClientProtocolException, IOException {
		HttpPut http = new HttpPut(url);
		setEntity(http);
		execute(http);
	}
	
	public void get() throws ClientProtocolException, IOException {
		if (param != null) {
			StringBuilder url = new StringBuilder(this.url);
			boolean isFirst = true;
			for (String key : param.keySet()) {
				if (isFirst) {
					url.append("?");
					isFirst = false;
				}else {
					url.append("&");
				}
				url.append(key).append("=").append(param.get(key));
			}
			this.url = url.toString();
		}
		HttpGet http = new HttpGet(url);
		execute(http);
	}
	
	/**
	 * set http post,put param
	 */
	private void setEntity(HttpEntityEnclosingRequestBase http) {
		if (param != null) {
			List<NameValuePair> nvps = new LinkedList<NameValuePair>();
			for (String key : param.keySet())
				nvps.add(new BasicNameValuePair(key, param.get(key))); // 参数
			http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8)); // 设置参数
		}
		if (xmlParam != null) {
			http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
		}
	}
	
	private void execute(HttpUriRequest http) throws ClientProtocolException,
			IOException {
		CloseableHttpClient httpClient = null;
		try {
			if (isHttps) {
				SSLContext sslContext = new SSLContextBuilder()
						.loadTrustMaterial(null, new TrustStrategy() {
							// 信任所有
							public boolean isTrusted(X509Certificate[] chain,
													 String authType)
									throws CertificateException {
								return true;
							}
						}).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslContext);
				httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
						.build();
			} else {
				httpClient = HttpClients.createDefault();
			}
			CloseableHttpResponse response = httpClient.execute(http);
			try {
				if (response != null) {
					if (response.getStatusLine() != null) {
						statusCode = response.getStatusLine().getStatusCode();
					}
					HttpEntity entity = response.getEntity();
					// 响应内容
					content = EntityUtils.toString(entity, Consts.UTF_8);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.close();
		}
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getContent() throws ParseException, IOException {
		return content;
	}
	
}
```

#### 2.3、添加商户APIv2 key

yml文件

```properties
# APIv2密钥
wxpay.partnerKey: T6m9iK73b0kn9g5v426MKfHQH7X8rKwb
```

WxPayConfig.java

```java
	//APIv2密钥
	private String partnerKey;
```

#### 2.4、添加枚举

enum WxApiType

```java
/**
* Native下单V2
*/
NATIVE_PAY_V2("/pay/unifiedorder"),
```

enum WxNotifyType

```java
/**
* 支付通知V2
*/
NATIVE_NOTIFY_V2("/api/wx-pay-v2/native/notify"),
```

### 3、统一下单

#### 3.1、创建WxPayV2Controller


```java
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
	public AjaxResult createNative(@PathVariable Long productId, HttpServletRequest
			request) throws Exception {
		log.info("发起支付请求 v2");
		String remoteAddr = request.getRemoteAddr();
		Map<String, Object> map = wxPayService.nativePayV2(productId,
				remoteAddr);
		return AjaxResult.ok().setData(map);
	}
}
```

#### 3.2、WxPayService

接口：

```java
	/**
	 * @Description 创建订单，调用Native支付接口V2
	 * @Param [productId, remoteAddr] 商品ID, 远程客户端主机主机地址
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 */
	Map<String, Object> nativePayV2(Long productId, String remoteAddr);
```

实现：

```java
	@Override
	public Map<String, Object> nativePayV2(Long productId, String remoteAddr) throws Exception {
		log.info("生成订单");
		//生成订单<与V3一致>
		OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);
		String codeUrl = orderInfo.getCodeUrl();
		if(orderInfo != null && !StringUtils.isEmpty(codeUrl)){
			log.info("订单已存在，二维码已保存");
			//返回二维码
			Map<String, Object> map = new HashMap<>();
			map.put("codeUrl", codeUrl);
			map.put("orderNo", orderInfo.getOrderNo());
			return map;
		}
		
		log.info("调用统一下单API");
		HttpClientUtils client = new
				HttpClientUtils("https://api.mch.weixin.qq.com/pay/unifiedorder");
		//组装接口参数
		Map<String, String> params = new HashMap<>();
		params.put("appid", wxPayConfig.getAppid());//关联的公众号的appid
		params.put("mch_id", wxPayConfig.getMchId());//商户号
		params.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
		params.put("body", orderInfo.getTitle());
		params.put("out_trade_no", orderInfo.getOrderNo());
		//注意，这里必须使用字符串类型的参数（总金额：分）
		String totalFee = orderInfo.getTotalFee() + "";
		params.put("total_fee", totalFee);
		params.put("spbill_create_ip", remoteAddr);
		params.put("notify_url",
				wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));
		params.put("trade_type", "NATIVE");
		//将参数转换成xml字符串格式：生成带有签名的xml格式字符串<V2依赖>
		String xmlParams = WXPayUtil.generateSignedXml(params, wxPayConfig.getPartnerKey());
		log.info("\n xmlParams：\n" + xmlParams);
		client.setXmlParam(xmlParams);//将参数放入请求对象的方法体
		client.setHttps(true);//使用https形式发送
		client.post();//发送请求
		String resultXml = client.getContent();//得到响应结果
		log.info("\n resultXml：\n" + resultXml);
		//将xml响应结果转成map对象
		Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
		//错误处理
		if("FAIL".equals(resultMap.get("return_code")) ||
				"FAIL".equals(resultMap.get("result_code"))){
			log.error("微信支付统一下单错误 ===> {} ", resultXml);
			throw new RuntimeException("微信支付统一下单错误");
		}
		//二维码
		codeUrl = resultMap.get("code_url");
		//保存二维码
		String orderNo = orderInfo.getOrderNo();
		orderInfoService.saveCodeUrl(orderNo, codeUrl);
		//返回二维码
		Map<String, Object> map = new HashMap<>();
		map.put("codeUrl", codeUrl);
		map.put("orderNo", orderInfo.getOrderNo());
		return map;
	}
```

### 4、支付回调

WxPayServiceImpl


```java
	/**
	 * @Description 支付通知:微信支付通过支付通知接口将用户支付成功消息通知给商户
	 * @Param [request]
	 * @return java.lang.String
	 */
	@PostMapping("/native/notify")
	public String wxNotify(HttpServletRequest request) throws Exception {
		log.info("微信发送的回调");
		Map<String, String> returnMap = new HashMap<>();//应答对象
		//处理通知参数
		String body = HttpUtils.readData(request);
		//验签<V2依赖>
		if(!WXPayUtil.isSignatureValid(body, wxPayConfig.getPartnerKey())) {
			log.error("通知验签失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "验签失败");
			//V2以XML方式传输
			return WXPayUtil.mapToXml(returnMap);
		}
		//解析xml数据
		Map<String, String> notifyMap = WXPayUtil.xmlToMap(body);
		//判断通信和业务是否成功
		if(!"SUCCESS".equals(notifyMap.get("return_code")) || !"SUCCESS".equals(notifyMap.get("result_code"))) {
			log.error("失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "失败");
			return WXPayUtil.mapToXml(returnMap);
		}
		//获取商户订单号
		String orderNo = notifyMap.get("out_trade_no");
		OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
		//并校验返回的订单金额是否与商户侧的订单金额一致
		if (orderInfo != null && orderInfo.getTotalFee() != Long.parseLong(notifyMap.get("total_fee"))) {
			log.error("金额校验失败");
			//失败应答
			returnMap.put("return_code", "FAIL");
			returnMap.put("return_msg", "金额校验失败");
			return WXPayUtil.mapToXml(returnMap);
		}
		//处理订单
		if(lock.tryLock()){
			try {
				//处理重复的通知
				//接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的。
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				if(OrderStatus.NOTPAY.getType().equals(orderStatus)){
					//更新订单状态
					orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
					//记录支付日志
					paymentInfoService.createPaymentInfo(body);
				}
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		returnMap.put("return_code", "SUCCESS");
		returnMap.put("return_msg", "OK");
		String returnXml = WXPayUtil.mapToXml(returnMap);
		log.info("支付成功，已应答");
		return returnXml;
	}
```

## 优化

在WxPayServiceImpl中,queryBill的获取账单下载地址紧凑写法(可读性差)

```java
			//获取账单下载地址
			/*Gson gson = new Gson();
			Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
			return resultMap.get("download_url");*/
			
			return (String)new Gson().fromJson(bodyAsString,HashMap.class).get("download_url");
```



# 支付宝

## ⼀、支付宝⽀付介绍和接⼊指引

### 1、准备⼯作

1.1、创建案例项⽬
1.2、⽀付安全相关知识

### 2、⽀付宝开放能⼒介绍

2.1、能⼒地图
⽀付能⼒、⽀付扩展、资⾦能⼒、⼝碑能⼒、营销能⼒、会员能⼒、⾏业能⼒、安全能⼒、基础能⼒



2.2、电脑⽹站⽀付产品介绍
应⽤场景、准⼊条件、计费模式

### 3、接⼊准备

3.1、开放平台账号注册
https://open.alipay.com/

3.2、常规接⼊流程

创建应⽤：选择应⽤类型、填写应⽤基本信息、添加应⽤功能、配置应⽤环境（获取⽀付宝公
钥、应⽤公钥、应⽤私钥、⽀付宝⽹关地址，配置接⼝内容加密⽅式）、查看 APPID

>   [秘钥生成器网页版(不确定安全性)](https://www.dedemao.com/alipay/rsa.php)

绑定应⽤：将开发者账号中的APPID和商家账号PID进⾏绑定
配置秘钥：即创建应⽤中的“配置应⽤环境”步骤
上线应⽤：将应⽤提交审核
签约功能：在商家中⼼上传==营业执照==、已备案⽹站信息等，提交审核进⾏签约

3.3、使⽤沙箱
沙箱环境配置：https://opendocs.alipay.com/common/02kkv7
沙箱版⽀付宝的下载和登录：https://open.alipay.com/platform/appDaily.htm?tab=tool

支付宝API调试工具:https://opendocs.alipay.com/open/02np8f

电脑网站支付调试页面:https://open.alipay.com/api/detail?code=I1080300001000041203

## ⼆、运⾏和配置案例项⽬

1、还原数据库
payment_demo.sql，执⾏以下命令还原数据库
2、运⾏后端项⽬
⽤idea打开payment-demo，确认maven仓库的位置，修改application.yml中的数据库连接配置，运⾏
项⽬
3、运⾏前端项⽬
安装node.js，如果你希望⽅便的查看和修改前端代码，可以安装⼀个VSCode和相关插件，⽤VSCode打
开前端项⽬payment-demo-front，运⾏前端项⽬
4、引⼊⽀付参数
4.1、引⼊沙箱配置⽂件
将之前准备好的 alipay-sandbox.properties 复制到项⽬的 resources ⽬录中

```properties
# 支付宝支付相关参数

# 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
alipay.app-id=2021003145652227

# 商户PID,卖家支付宝账号ID
alipay.seller-id=2088621993133371

# 支付宝网关
alipay.gateway-url=https://openapi.alipaydev.com/gateway.do

# 商户私钥，您的PKCS8格式RSA2私钥(沙箱自动生成)
alipay.merchant-private-key=MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCPYePkkToFj8WhXLrssxoHCoCg5AKFVbcwGX7dYSz8VEI5a6v/eZqweFaODz7TpzvcIDetWvkkjwQA5I6EvN0waUwB0BjN10oIpP6iS5vdICbY3Zja1n5el4FcWrXaB//Dt5lyM5UNJnaIW8LD3pC6DE4pMhXuZW2aV3Lo3rZzqZhDh2q3oNFm6r7m9l66VRaajyJLmlN8czfkYIeqRNdouRjs86F0mRuoTeb/g8sJD6WuGtBUEmd7rcb+Hfe5Zo2wUeOaQ9Es+CAKSmK4VnaeBRR+TmLK3DIyCb3vWSqaxwAyOP/h1yG2GFlePDpOh1PIW1WBXVNFcM/k2Rnl9F6XAgMBAAECggEAYxX5EXmzKSjEINEedHkJIZiGb6lifJZRtDHKVF7VYcMwyNG52SFGX3GqDx5GAqptWaACGhDmX/ddguhv/RcvnEcIObB/k7CV/wdW2P17RwLzFad9/K2CQwhcY2Bkj7o7bBuzNRKkjKPqAi23mPhz976NPTbZpRf++Ew6oudWGvMtEBtejwkVOAkazFs/eJwXqLxN2ozPQkjRMhn4QWdTiqFPKPe5jVW0XF/PTRwR2JdYZHmQAo3sy5qVymuKvJes9k222dPu/EUGGQeTfZw0CdbgnP1VDG0dkhDgbPORIYXfx5XLNrgQx+PBlFgZcb+6Yg22qZhM7mwvUAz4UJOZ8QKBgQDP7XVqSNdzbk65dxHCDYGAN3Cu5ZPkjPFitSXRCVW8DCO6Sb3CZ/p8TpULVzOiRX/TvcrYiU9IULarsSLjDiOjDAydr7OIbaw0Hp58rqokk+fTmENirLuK8Xb6z8nnzqPy8Byyfdi82j7/dp87SN1GWlLnKwuW+a1F9dPD/OaaUwKBgQCwiDdttDCF57TuvWY5VAdfsHTFl4DGvBeJFfqXAEJuHStacR0CQhW10wAXmi9+cLCh+RLuDs6Q5eVLIzC0awkph/WRHoRASj7OR14YXFBYeMXi8bLbEqQM7As5hU3Q9kPCyNKLYkeKJ2teW7pbNA2UjCj2aDttjZYJuSPVjowKLQKBgQCs43hW6Lxk2ZqR8iSM4ygD0ZVbh78iMeNgPNl157onAz0N39Tt+gE4LSsW4+omCn3QNSaeSAXpTlulhUNJ4m7VWuZ+kyLH1NF0AOWtLJqCkY0YzqlxOckzLbXNtMrgeVyvWuGxtJxr41iwmBSsedirb90xJASeA3sFCfOMKhQgywKBgQCNmeYkOEpHztGiQwhxaca0ad1w9ZLVVuDmFFw6rXmbzAgPdvYW7p1tiL/lLsdz+76jXiBiFjx5m/7t419ZCRaDWsyOyFS7IRL58eM3VS0Zml7ZKJkYAv7WYqyqShReTnuSDfZc2cuatP355Ug5iipugnMFHBy3RsAYMorc7YfFTQKBgF+B78mIc35W9WGRGyi7TUwXkDssDKr2vo/ICVWK9tksNbpRfswdLkbgiz4O5+LOcB8OF2GCMmBn567qDHKcn+yb4l8AO7cyopGU6QvH7zutuwoLsb84gOXHl0cAeqDaMFnHKiv3N6/1UGZeLwZHZD2iQd4oeaujbXZcVAc0e1KI

# 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥
alipay.alipay-public-key=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi0C8ClFOJpsSarD1aPgzPrv9fDyk+x+iIiVlwVVOGsipnwOjY7ozzTrJjK0mCGTcfFCabivudCq0kf6Pz5+d6Ownlm1biN6xgZIJIj6iqrI3C8TjDOhySSpwU/sN1gngUckBVJlH015G5juXEI9om1nTQGfTNF/rag4dlZsZStp0MxWfQacR8x0Xt/ka9g5Bc+2KpW+Cd15DQ08SeL18iIEwIs1gSANlxJaG5RbubjT8e5kWZkPmV/1+iC4fm8+oCUzoLmp1ESmg1DZdCRwXQ/TddoCGntjHH7mo9uEUCF18Gf9UO0lc7Gr7xU9ubCoevPUP9Z5CVck7SJTZktM1oQIDAQAB

# 接口内容加密秘钥，对称秘钥
alipay.content-key=5j2FWUDBZjRtvbdHR8fUVQ==

# 页面跳转同步通知页面路径
alipay.return-url=http://localhost:8080/#/success

# 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
# 注意：每次重新启动ngrok，都需要根据实际情况修改这个配置
alipay.notify-url=https://alipay.loca.lt/api/ali-pay/trade/notify
```

并将其设置为 spring 配置⽂件
4.2、创建配置⽂件
在config包中创建AlipayClientConfig

```java
@Configuration
//加载配置文件
@PropertySource("classpath:alipay-sandbox.properties")
public class AlipayClientConfig {
    //与微信不同,使用更简便方法
}
```

4.3、测试配置⽂件的引⼊

```java
@SpringBootTest
@Slf4j
class AlipayTests {
	
	//自动从上下文中获取加载过的文件
	@Resource
	private Environment config;
	@Test
	void testGetAlipayConfig(){
		log.info("appid = " + config.getProperty("alipay.app-id"));
	}
}
```

>   可能需要将target删除后在build来获取Alipay配置

5、引⼊服务端SDK
5.1、引⼊依赖
参考⽂档：开放平台 => ⽂档 => 开发⼯具 => 服务端SDK => Java => 通⽤版 => Maven项⽬依赖
https://search.maven.org/artifact/com.alipay.sdk/alipay-sdk-java

```xml
        <!--支付宝支付SDK-->
        <dependency>
            <groupId>com.alipay.sdk</groupId>
            <artifactId>alipay-sdk-java</artifactId>
            <version>4.33.26.ALL</version>
        </dependency>
```

5.2、创建客⼾端连接对象
创建带数据签名的客⼾端对象
参考⽂档：开放平台 => ⽂档 => 开发⼯具 => 技术接⼊指南 => 数据签名

https://opendocs.alipay.com/common/02kf5q
参考⽂档中**公钥方式**完善 AlipayClientConfig 类，添加 alipayClient() ⽅法 初始化 AlipayClient 对象

```java
	//与微信不同,使用更简便方法
	//自动获取alipay-sandbox.properties中的配置
	@Resource
	private Environment config;
	
	//使用支付宝支付SDK
	//https://opendocs.alipay.com/common/02kf5q
	@Bean
	public AlipayClient alipayClient() throws AlipayApiException {
		AlipayConfig alipayConfig = new AlipayConfig();
		//设置网关地址
		alipayConfig.setServerUrl(config.getProperty("alipay.gateway-url"));
		//设置应用Id
		alipayConfig.setAppId(config.getProperty("alipay.app-id"));
		//设置应用私钥
		alipayConfig.setPrivateKey(config.getProperty("alipay.merchant-privatekey"));
		//设置请求格式，固定常量值json
		alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
		//设置字符集
		alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
		//设置支付宝公钥
		alipayConfig.setAlipayPublicKey(config.getProperty("alipay.alipay-publickey"));
		//设置签名类型
		alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
		//构造client
		AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
		return alipayClient;
	}
```

## 三、⽀付功能开发

### 1、统⼀收单下单并⽀付⻚⾯

1.1、⽀付调⽤流程
https://opendocs.alipay.com/open/270/105899

![img](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/0ba3e82ad37ecf8649ee4219cfe9d16b.png%2526originHeight%253D2023%2526originWidth%253D2815%2526size%253D526149%2526status%253Ddone%2526width%253D2815)

1.2、接⼝说明
https://opendocs.alipay.com/apis/028r8t?scene=22
公共请求参数：所有接⼝都需要的参数
请求参数：当前接⼝需要的参数
公共响应参数：所有接⼝的响应中都包含的数据
响应参数：当前接⼝的响应中包含的数据

1.3、发起⽀付请求
（1）创建 AliPayController

```java
/**
 * @name AliPayController
 * @Description 控制器:支付宝
 * @author WU
 * @Date 2022/8/31 17:44
 */
@CrossOrigin //开放前端的跨域访问
@RestController
@RequestMapping("/api/ali-pay")
@Api(tags = "网站支付宝支付")
@Slf4j
public class AliPayController {
	@Resource
	private AliPayService aliPayService;
	
	@ApiOperation("统一收单下单并支付页面接口的调用")
	@PostMapping("/trade/page/pay/{productId}")
	public AjaxResult tradePagePay(@PathVariable Long productId){
		log.info("统一收单下单并支付页面接口的调用");
		//支付宝开放平台接受 request 请求对象后
		// 会为开发者生成一个html 形式的 form表单，包含自动提交的脚本
		String formStr = aliPayService.tradeCreate(productId);
		//我们将form表单字符串返回给前端程序，之后前端将会调用自动提交脚本，进行表单的提交
		//此时，表单会自动提交到action属性所指向的支付宝开放平台中，从而为用户展示一个支付页面
		return AjaxResult.ok().data("formStr", formStr);
	}
}
```

（2）创建 AliPayService
接⼝

```java
/**
 * @name AliPayService
 * @Description
 * @author WU
 * @Date 2022/8/31 17:51
 */
public interface AliPayService {
	
	/**
	 * @Description 创建交易
	 * @Param [productId] 商品ID
	 * @return java.lang.String
	 */
	String tradeCreate(Long productId);
	
}
```

实现

```java
/**
 * @name AliPayServiceImpl
 * @Description
 * @author WU
 * @Date 2022/8/31 17:51
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
	
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private AlipayClient alipayClient;
	@Resource
	private Environment config;
	@Transactional(rollbackFor = Exception.class)//需显示指定rollback
	@Override
	public String tradeCreate(Long productId) {
		try {
			//1.生成订单
			log.info("生成订单");
			OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

			//调用支付宝接口
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			//配置需要的公共请求参数
			//request.setNotifyUrl("");
			//支付完成后，我们想让页面跳转回支付成功的页面，配置returnUrl
			request.setReturnUrl(config.getProperty("alipay.return-url"));
			//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
			//先转成String再转成BigDecimal
			BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));

				/*//组装当前业务方法的请求参数
				JSONObject bizContent = new JSONObject();
				bizContent.put("out_trade_no", orderInfo.getOrderNo());
				//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
				//先转成String再转成BigDecimal
				BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
				bizContent.put("total_amount", total);//订单总金额
				bizContent.put("subject", orderInfo.getTitle());//订单标题
				bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//销售产品码
			//将bizContent从JSONObject --> JSON(String)
			request.setBizContent(bizContent.toString())*/;
			
			//另解(支付宝封装,不易出错)
			AlipayTradePagePayModel model = new AlipayTradePagePayModel();
			model.setOutTradeNo(orderInfo.getOrderNo());
			model.setTotalAmount(total.toString());//需用回String
			model.setSubject(orderInfo.getTitle());
			model.setProductCode("FAST_INSTANT_TRADE_PAY");//销售产品码(当前仅支持该常量:新快捷即时到账产品)
			request.setBizModel(model);
			
			
			//执行请求，调用支付宝接口
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			if(response.isSuccess()){
				log.info("调用成功，返回结果 ===> " + response.getBody());
				return response.getBody();
			} else {
				log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
				throw new RuntimeException("创建支付交易失败");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new RuntimeException("创建支付交易失败");
		}
	}

}
```

返回的form:

```html
<form name=\"punchout_form\" method=\"post\" action=\"https://openapi.alipaydev.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=LpNFzhOaYMbVpIvXDFOhgBCjF46gRYboeN3ylcg1sNBpxWNssA9vPTgBKfl9zFqeKS2PBNpM%2B%2FXl8YBa%2BbZMAFUj5EKYl8tejGYFYIwX5lDVrE2y5hmIYA%2FY2doJfmUFl1%2B%2BtLEowIiLyMl8IWmqQ9bbftwR%2BA%2BusoLD%2FxdlKFC1fGF5uJReiS3UWALwIK0WuKmUjim3nQMXHCdyX%2BCS1a%2F0InLPSQwA03l5H%2Fx6fvcbaxjT6t3%2BSF7Z7qHFjRxyhwjDN4nHFP%2BzZvfQYtAjE4weh6StTd%2BwLC79lXsvi5Vfcy5EVAfPZwzhpQdnc6rHmeEeMyWukZR4nXraYh%2F5jw%3D%3D&return_url=http%3A%2F%2Flocalhost%3A8080%2F%23%2Fsuccess&version=1.0&app_id=2021003145652227&sign_type=RSA2&timestamp=2022-08-31+20%3A05%3A02&alipay_sdk=alipay-sdk-java-dynamicVersionNo&format=json\">
    <input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;HSXY_ORDER_20220831200501732&quot;,&quot;total_amount&quot;:0.2,&quot;subject&quot;:&quot;Java课程&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">
    <input type=\"submit\" value=\"立即支付\" style=\"display:none\" >
</form>
<script>
    document.forms[0].submit();
</script>
```

该脚本会自动打开支付宝付款界面

>   无法通过浏览器手动打开
>
>   ![image-20220831202413956](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220831202413956.png)

支付界面:

![image-20220831202520818](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220831202520818.png)

1.4、前端⽀付按钮
（1）index.vue

```js
//确认支付
toPay() {
	//禁用按钮，防止重复提交
	this.payBtnDisabled = true
	//微信支付
	if (this.payOrder.payType === 'wxpay') {
		......
		//支付宝支付
	} else if (this.payOrder.payType === 'alipay') {
		//调用支付宝统一收单下单并支付页面接口
		aliPayApi.tradePagePay(this.payOrder.productId).then((response) => {
			//将支付宝返回的表单字符串写在浏览器中，表单会自动触发submit提交
			document.write(response.data.formStr)
		})
	}
},
```

（2）aliPay.js

```js
// axios 发送ajax请求
import request from '@/utils/request'
export default{
	//发起支付请求
	tradePagePay(productId) {
		return request({
			url: '/api/ali-pay/trade/page/pay/' + productId,
			method: 'post'
		})
	}
}
```



### 2、⽀付结果通知

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904091554623.png" alt="image-20220904091554623" style="zoom: 50%;" />

2.1、设置异步通知地址
在AliPayServiceImpl 的tradeCreate ⽅法中设置异步通知地址

```java
			//通知回调接口:避免输入一大串(与微信不同,一体化简化长度)
			// 微信 :https://wxpay.loca.lt/api/wx-pay/native/notify
			//支付宝:https://alipay.loca.lt/api/ali-pay/trade/notify [!]支付通知需和尾部一致
			request.setNotifyUrl(config.getProperty("alipay.notify-url"));
```

2.2、启动内⽹穿透ngrok
2.3、修改内⽹穿透配置
根据ngrok每次启动的情况，修改alipay-sandbox.properties ⽂件中的alipay.notify-url
2.4、开发异步通知接⼝
（1）AliPayController

```java
	@Resource
	private Environment config;
	@Resource
	private OrderInfoService orderInfoService;
	
	/**
	 * @Description 支付通知
	 * @Param [params] 支付宝返回参数(与微信不同,支付宝直接返回Map[更方便],而微信则返回JSON)
	 * @return java.lang.String
	 */
	@ApiOperation("支付通知")
	@PostMapping("/trade/notify")//需和回调后缀一致
	public String tradeNotify(@RequestParam Map<String, String> params){
		log.info("支付通知正在执行");
		log.info("通知参数 ===> {}", params);
		String result = "failure";
		try {//rsaCheckV1异常处理
			//异步通知验签
			boolean signVerified = AlipaySignature.rsaCheckV1(
					params,
					config.getProperty("alipay.alipay-public-key"),
					AlipayConstants.CHARSET_UTF8,
					AlipayConstants.SIGN_TYPE_RSA2); //调用SDK验证签名
			if(!signVerified){
			//验签失败则记录异常日志，并在response中返回failure.
				log.error("支付成功异步通知验签失败！");
				return result;
			}
			// 验签成功后
			log.info("支付成功异步通知验签成功！");
			
			//一.按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验(支付宝以明文形式返回支付通知<可设置为密文>)
			//1 商户需要验证该通知数据中的 out_trade_no 是否为商户系统中创建的订单号
			String outTradeNo = params.get("out_trade_no");
			OrderInfo order = orderInfoService.getOrderByOrderNo(outTradeNo);
			if(order == null){
				log.error("订单不存在");
				return result;
			}
			//2 判断 total_amount 是否确实为该订单的实际金额（即商户订单创建时的金额）
			String totalAmount = params.get("total_amount");
			//String先转成BigDecimal 改为分(数据库单位为分) 再转成int
			int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();//返回金额
			/**BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));*/
			int totalFeeInt = order.getTotalFee();//订单金额
			if(totalAmountInt != totalFeeInt){
				log.error("金额校验失败");
				return result;
			}
			//3 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方
			String sellerId = params.get("seller_id");//返回商户id
			String sellerIdProperty = config.getProperty("alipay.seller-id");//商户id
			if(!sellerId.equals(sellerIdProperty)){
				log.error("商家pid校验失败");
				return result;
			}
			//4 验证 app_id 是否为该商户本身
			String appId = params.get("app_id");//返回app-id
			String appIdProperty = config.getProperty("alipay.app-id");//app-id
			if(!appId.equals(appIdProperty)){
				log.error("appid校验失败");
				return result;
			}
			//在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS时，(支持退款时)
			// 支付宝才会认定为买家付款成功。
			//!响应参数与微信不同(trade_state)
			String tradeStatus = params.get("trade_status");//返回交易状态
			if(!"TRADE_SUCCESS".equals(tradeStatus)){
				log.error("支付未成功");
				return result;
			}
			
			//二.处理业务 修改订单状态 记录支付日志
			aliPayService.processOrder(params);
			//校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
			result = "success";
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return result;
	}
```



（2）AliPayService
接⼝

```java
void processOrder(Map<String, String> params);
```

实现

```java
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
	
	@Resource
	private OrderInfoService orderInfoService;
	@Resource
	private AlipayClient alipayClient;
	//自动从上下文中获取加载过的文件
	@Resource
	private Environment config;
	
	@Transactional(rollbackFor = Exception.class)//需显示指定rollback
	@Override
	public String tradeCreate(Long productId) {
		try {
			//1.生成订单
			log.info("生成订单");
			OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);
			//log.info(config.getProperty("wxpay.domain"));//可以获取微信的配置
			//调用支付宝接口
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			//配置需要的公共请求参数
			//通知回调接口:避免输入一大串(与微信不同,一体化简化长度)
			// 微信 :https://wxpay.loca.lt/api/wx-pay/native/notify
			//支付宝:https://alipay.loca.lt/api/ali-pay/trade/notify [!]支付通知需和尾部一致
			request.setNotifyUrl(config.getProperty("alipay.notify-url"));
			
			//支付完成后，我们想让页面跳转回支付成功的页面，配置returnUrl
			request.setReturnUrl(config.getProperty("alipay.return-url"));
			
			//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
			//先转成String再转成BigDecimal
			BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
			/**int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();*/

				/**组装当前业务方法的请求参数
				JSONObject bizContent = new JSONObject();
				bizContent.put("out_trade_no", orderInfo.getOrderNo());
				//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
				//先转成String再转成BigDecimal
				BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
				bizContent.put("total_amount", total);//订单总金额
				bizContent.put("subject", orderInfo.getTitle());//订单标题
				bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//销售产品码
			//将bizContent从JSONObject --> JSON(String)
			request.setBizContent(bizContent.toString())*/;
			
			//另解(支付宝封装,不易出错)
			AlipayTradePagePayModel model = new AlipayTradePagePayModel();
			model.setOutTradeNo(orderInfo.getOrderNo());
			model.setTotalAmount(total.toString());//需用回String
			model.setSubject(orderInfo.getTitle());
			model.setProductCode("FAST_INSTANT_TRADE_PAY");//销售产品码(当前仅支持该常量:新快捷即时到账产品)
			request.setBizModel(model);
			
			
			//执行请求，调用支付宝接口
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			if(response.isSuccess()){
				log.info("调用成功，返回结果 ===> " + response.getBody());
				return response.getBody();
			} else {
				log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
				throw new RuntimeException("创建支付交易失败");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new RuntimeException("创建支付交易失败");
		}
	}
}
```

2.5、记录⽀付⽇志
PaymentInfoService
接⼝

```java
void createPaymentInfoForAliPay(Map<String, String> params);
```

实现

```java
	@Override
	public void createPaymentInfoForAlipay(Map<String, String> params) {
		log.info("记录支付日志");
		/**Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> params = gson.fromJson(plainText, HashMap.class);*/
		//-商户订单号
		String orderNo = params.get("out_trade_no");
		//-支付宝支付订单号
		String transactionId = params.get("trade_no");//区别于微信
		//-交易类型
		//String tradeType = params.get("trade_type");//直接写
		//-交易状态
		String tradeStatus = params.get("trade_status");
		log.info("交易状态---> {}" , tradeStatus);
		//-订单金额
		String totalAmount = params.get("total_amount");
		//String先转成BigDecimal 改为分(数据库单位为分) 再转成int
		int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();//返回金额
		/**Map<String, Object> amount = (Map)params.get("amount");
		//坑:不能直接转为int(Gson缺陷) || 实际中应转换为BigDecimal(不要转为Double,会有精度损失[此处支付金额单位为(分)应该没事?])
		//用户实际支付金额
		//Integer payerTotal = ((BigDecimal) amount.get("payer_total")).intValue();//报错
		Integer payerTotal = ((Double) amount.get("payer_total")).intValue();*/
		log.info("订单实际金额---> {}" , totalAmount);
		
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setOrderNo(orderNo);
		paymentInfo.setPaymentType(PayType.ALIPAY.getType());
		paymentInfo.setTransactionId(transactionId);
		paymentInfo.setTradeType("电脑网站支付");
		paymentInfo.setTradeState(tradeStatus);
		paymentInfo.setPayerTotal(totalAmountInt);
		
		//改为JSON格式
		String json = new Gson().toJson(params, HashMap.class);
		//备份返回报文(各支付厂商可能返回类型不同,以备不时之需)
		paymentInfo.setContent(json);
		
		baseMapper.insert(paymentInfo);
	}
```

注:交易状态参数与微信不同

![image-20220901144813677](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220901144813677.png)

2.6、更新订单状态记录⽀付⽇志

![image-20220901163939005](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220901163939005.png)

在 processOrder ⽅法中，更新订单状态之前，添加代码

2.7、数据锁
在AliPayServiceImpl 中定义 ReentrantLock 进⾏并发控制。注意，必须⼿动释放锁。
完整的processOrder ⽅法

```java
	@Resource
	private PaymentInfoService paymentInfoService;

	/**
	 * @Description 锁
	 */
	private final ReentrantLock lock = new ReentrantLock();
	@Override
	public void processOrder(Map<String, String> params) {

		log.info("处理订单");
		/**支付宝无需转换,返回参数params即为Map
		Gson gson = new Gson();
		//转换明文:将明文转换为Map
		Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);*/
		//商户订单号
		String orderNo = params.get("out_trade_no");
		
		/*在对业务数据进行状态检查和处理之前，
		要采用数据锁进行并发控制，
		以避免函数重入造成的数据混乱*/
		//尝试获取锁：
		// 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
		if(lock.tryLock()){
			try {
				//处理重复通知
				//保证接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
				String orderStatus = orderInfoService.getOrderStatus(orderNo);
				if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
					return;
				}
				
				//更新订单状态
				orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
				//记录支付日志
				paymentInfoService.createPaymentInfoForAlipay(params);
			} finally {
				//要主动释放锁
				lock.unlock();
			}
		}
		
	}
```

### 3、订单表优化

3.1、表修改
t_order_info 表中添加payment_type 字段

```java
    private String paymentType;//支付方式
```

3.2、业务修改
（1）修改⽀付业务代码

修改AliPayServiceImpl 、WxPayServiceImpl 代码中对如下⽅法的调⽤， 添加参数
PayType.ALIPAY.getType()

```java
		log.info("生成订单");
		OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId, PayType.ALIPAY.getType());
```

（2）修改OrderInfoService

```java
OrderInfo createOrderByProductId(Long productId, String paymentType);
```

接⼝的createOrderByProductId ⽅法中添加参数 String paymentType

```java
OrderInfo createOrderByProductId(Long productId, String paymentType);
```

实现类的createOrderByProductId ⽅法中添加参数String paymentType
对getNoPayOrderByProductId ⽅法的调⽤时添加参数paymentType
⽣成订单的过程中添加orderInfo.setPaymentType(paymentType);

```java
	@Override
	public OrderInfo createOrderByProductId(Long productId, String paymentType) {
		
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
		orderInfo.setPaymentType(paymentType);
		
		//baseMapper <== private OrderInfoMapper orderInfoMapper;
		baseMapper.insert(orderInfo);
		
		return orderInfo;
	}
```

对getNoPayOrderByProductId ⽅法的定义时添加参数paymentType
添加查询条件queryWrapper.eq("payment_type", paymentType);

### 4、统⼀收单交易关闭

4.1、定义⽤⼾取消订单接⼝
在AliPayController 中添加⽅法

```java
	@ApiOperation("用户取消订单")
	@PostMapping("/trade/close/{orderNo}")
	public AjaxResult cancel(@PathVariable String orderNo) throws Exception {
		log.info("取消订单");
		aliPayService.cancelOrder(orderNo);
		return AjaxResult.ok().setMessage("订单已取消");
	}
```

4.2、关单并修改订单状态
AliPayService 接⼝

```java
	void cancelOrder(String orderNo) throws Exception;
```

AliPayServiceImpl 实现

```java
	@Override
	public void cancelOrder(String orderNo) throws Exception {
		//调用支付宝支付的关单接口
		this.closeOrder(orderNo);
		//更新商户端的订单状态
		orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
	}
```

4.3、调⽤⽀付宝接⼝
AliPayServiceImpl 中添加辅助⽅法

```java
	/**
	 * @Description 支付宝支付端关单接口的调用
	 * @Param [orderNo]
	 * @return void
	 */
	private void closeOrder(String orderNo) throws Exception {
		log.info("关单接口的调用，订单号 ===> {}", orderNo);
		//复制请求示例:https://opendocs.alipay.com/open/028wob?ref=api
		AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
		JSONObject bizContent = new JSONObject();
		//此时还未付款只有商户订单号(不用trade_no)
		bizContent.put("out_trade_no", orderNo);
		request.setBizContent(bizContent.toString());
		AlipayTradeCloseResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("调用成功，返回结果 ===> " + response.getBody());
		} else {
			log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
			//当未登陆支付宝或未扫码时,支付宝未创建订单,也就没有取消订单的说法了
			//throw new RuntimeException("关单接口的调用失败");
		}
		
	}
```

4.4、测试
注意：针对⼆维码⽀付，只有经过扫码的订单才在⽀付宝端有交易记录。针对⽀付宝账号⽀付，只有经
过登录的订单才在⽀付宝端有交易记录。

### 5、统⼀收单线下交易查询

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904091456369.png" alt="image-20220904091456369" style="zoom:50%;" />

#### 5.1、查单接⼝的调⽤

商⼾后台未收到异步⽀付结果通知时，商⼾应该主动调⽤《统⼀收单线下交易查询接⼝》，同步订单状
态。
（1）AliPayController

```java
	@ApiOperation("查询订单：测试订单状态用")
	@GetMapping("/trade/query/{orderNo}")
	public AjaxResult queryOrder(@PathVariable String orderNo) throws AlipayApiException {
		log.info("查询订单");
		//将订单信息保存为字符串
		String bodyAsString = aliPayService.queryOrder(orderNo);
		return AjaxResult.ok().setMessage("查询成功").data("bodyAsString", bodyAsString);
	}
```
（2）AliPayService
接⼝

```java
	/**
	 * @Description 通过支付宝端查询订单
	 * @Param [orderNo] 订单号
	 * @return java.lang.String 返回订单查询结果，如果返回null则表示支付宝端尚未创建订单
	 */
	String queryOrder(String orderNo) throws AlipayApiException;
```

实现

```java
	@Override
	public String queryOrder(String orderNo) throws AlipayApiException {
		log.info("查单接口调用 ===> {}", orderNo);
		
		
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		JSONObject bizContent = new JSONObject();
		bizContent.put("out_trade_no", orderNo);
		request.setBizContent(bizContent.toString());
		AlipayTradeQueryResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("成功, 返回结果 = " + response.getBody());
			return response.getBody();
		} else {
			log.warn("调用失败,响应码 = " + response.getCode() + ",返回结果 = " + response.getBody());
			//throw new AlipayApiException("查单接口调用失败");
			//交易不存在
			return null;
		}
	}
```

![image-20220904114241172](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904114241172.png)

测试:

![image-20220904114328967](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904114328967.png)

#### 5.2、定时查单

（1）~~创建AliPayTask~~


~~（2）修改OrderInfoService~~(不改)

接⼝添加参数String paymentType
实现添加参数String paymentType ， 添加查询条件queryWrapper.eq("payment_type",paymentType);
~~（3）修改WxPayTask~~(不改)
将之前微信⽀付的⽅法调⽤也做⼀个优化
orderConfirm ⽅法中对getNoPayOrderByDuration 的调⽤添加参数 PayType.WXPAY.getType()

将定时任务整合:

```java
/**
 * @name PayTask
 * @Description 定时任务整合
 * @author WU
 * @Date 2022/9/4 11:53
 */
@Slf4j
@Component
public class PayTask {
	@Resource
	private OrderInfoService orderInfoService;
	
	@Resource
	private WxPayService wxPayService;
	
	@Resource
	private AliPayService aliPayService;
	
	/**
	 * @Description 订单确认:从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
	 * @Param []
	 * @return void
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void orderConfirm() throws Exception {
		log.info("orderConfirm 定时被执行......");
		//找出创建超过5分钟，并且未支付的订单
		List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(1);
		//从支付服务器方获取支付状态
		for (OrderInfo orderInfo : orderInfoList) {
			String orderNo = orderInfo.getOrderNo();
			String paymentType = orderInfo.getPaymentType();
			log.warn("超时订单 ===> {}", orderNo);
			
			if ( PayType.WXPAY.getType().equals(paymentType) ) {
				//核实订单状态：调用微信支付查单接口
				wxPayService.checkOrderStatus(orderNo);
			}
			else if ( PayType.ALIPAY.getType().equals(paymentType) ){
				//核实订单状态：调用支付宝支付查单接口
				aliPayService.checkOrderStatus(orderNo);
			}
			
		}
	}
	
}
```

#### 5.3、处理查询到的订单

（1）PayTask
在定时任务的for循环最后添加以下代码

```java
				//核实订单状态：调用支付宝支付查单接口
				aliPayService.checkOrderStatus(orderNo);
```

（2）AliPayService
核实订单状态

接⼝：

```java
	/**
	 * @Description 根据订单号查询支付宝支付查单接口，核实订单状态
	 * * 如果订单未创建，则更新商户端订单状态
	 * * 如果订单已支付，则更新商户端订单状态，并记录支付日志
	 * * 如果订单未支付，则调用关单接口关闭订单，并更新商户端订单状态
	 * @Param [orderNo] 订单号
	 * @return void
	 */
	void checkOrderStatus(String orderNo) throws Exception;
```

实现：

```java
	@Override
	public void checkOrderStatus(String orderNo) throws Exception {
		log.warn("根据订单号核实订单状态 ===> {}", orderNo);
		//调用支付宝支付查单接口
		String result = this.queryOrder(orderNo);
		//判断订单状态
		//1.订单未创建
		if(result == null){
			log.warn("核实订单未创建 ===> {}", orderNo);
			//只更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
		//根据支付宝文档提供的响应JSON
		HashMap<String, LinkedTreeMap> resultMap = new Gson().fromJson(result, HashMap.class);
			//获取嵌套JSON数据
		LinkedTreeMap alipayTradeQueryResponse = resultMap.get("alipay_trade_query_response");
		//获取微信支付端的订单状态
		String tradeStatus = (String)alipayTradeQueryResponse.get("trade_status");
		
		//2.订单未支付
		if(AliTradeState.WAIT_BUYER_PAY.getType().equals(tradeStatus)){
			log.warn("核实订单未支付 ===> {}", orderNo);
			//如果订单未支付，则调用关单接口
			this.closeOrder(orderNo);
			//更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
		}
		//3.订单已支付
		else if(AliTradeState.TRADE_SUCCESS.getType().equals(tradeStatus)){
			log.warn("核实订单已支付 ===> {}", orderNo);
			//如果确认订单已支付则更新本地订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
			//记录支付日志(从嵌套JSON中获取)
			paymentInfoService.createPaymentInfoForAlipay(alipayTradeQueryResponse);
		}
    }
```

result响应示例JSON:

```json
{
    "alipay_trade_query_response": {
        "code": "10000",
        "msg": "Success",
        "buyer_logon_id": "ypv***@sandbox.com",
        "buyer_pay_amount": "0.00",
        "buyer_user_id": "2088622987601270",
        "buyer_user_type": "PRIVATE",
        "invoice_amount": "0.00",
        "out_trade_no": "HSXY_ORDER_20220904113029064",
        "point_amount": "0.00",
        "receipt_amount": "0.00",
        "total_amount": "0.01",
        "trade_no": "2022090422001401270501797287",
        "trade_status": "TRADE_CLOSED"
    },
    "sign": "M2cRq5qqv0R067bXXySoBcTk7SPa4l2yCOKpYMeL7IyRrfP/rl/aVK7RWV7kF+00gli3FxKFIUYiHR0sOIc7OePq1j+9sxH0Ti5Fi8etKRqFRV7j5vtKfvbtpk4AIYUiqFbhLmPXJoYz73uLt1zPXEewnUWXLn56EYASsQktLCBdJBtwA9XxmfSRch52eoAedeCwL8MiY7a4szGyZJtfC3z52zXpcWzJnVBbZBxJv8bqagPqgJM5c/XYyxwFoJHtO/iYt6GBTgMM5m0wDVFf06Skn0GL+k+O2AYD4x/vmV19WmNi3jCvlDb1XWF5G/d8nLPU/eN5yU053Pz0vDAiJw=="
}
```

### 6、统⼀收单交易退款

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904223227978.png" alt="image-20220904223227978" style="zoom: 67%;" />

6.1、退款接⼝
（1）AliPayController

```java
	/**
	 * @Description 申请退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("申请退款")
	@PostMapping("/trade/refund/{orderNo}/{reason}")
	public AjaxResult refunds(@PathVariable String orderNo, @PathVariable String reason) throws AlipayApiException {
		log.info("申请退款");
		aliPayService.refund(orderNo, reason);
		return AjaxResult.ok();
	}
```

（2）AliPayService
接⼝

```java
	/**
	 * @Description 申请退款
	 * @Param [orderNo, reason] 订单号,原因
	 * @return void
	 */
	void refund(String orderNo, String reason) throws AlipayApiException;
```

实现

```java
	@Resource
	private RefundInfoService refundsInfoService;
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void refund(String orderNo, String reason) throws AlipayApiException {
		log.info("创建退款单记录");
		//根据订单编号创建退款单
		RefundInfo refundInfo = refundsInfoService.createRefundByOrderNo(orderNo, reason);
		log.info("调用退款API");
		//调用统一收单API
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest ();
		
		//金额使用大浮点数<区别于微信,微信以分为单位,但支付宝以元为单位>
		//先转成String再转成BigDecimal
		BigDecimal refund = new BigDecimal(refundInfo.getTotalFee().toString()).divide(new BigDecimal("100"));
		
		//另解(支付宝封装,不易出错)
		//+AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setOutTradeNo(refundInfo.getOrderNo());
		model.setRefundAmount(refund.toString());//需用回String
		model.setRefundReason(reason);//退款原因(可选)
		//+model.setSubject(refundsInfo.);
		//+model.setProductCode("FAST_INSTANT_TRADE_PAY");//销售产品码(当前仅支持该常量:新快捷即时到账产品)
		//内置sdk.biz.info,输出日志
		request.setBizModel(model);
		
		//执行请求，调用支付宝接口
		//+AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("调用成功，返回结果 ===> " + response.getBody());
			
			//更新订单状态
			//!区别于微信, 非银行卡类同步返回结果,可直接设置
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
			//更新退款单
			refundsInfoService.updateRefundForAliPay(
					refundInfo.getRefundNo(),
					response.getBody(),
					AlipayTradeState.REFUND_SUCCESS.getType()); //退款成功
		} else {
			log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
			//+throw new RuntimeException("创建支付交易失败");
			//更新订单状态
			orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_ABNORMAL);
			//更新退款单
			refundsInfoService.updateRefundForAliPay(
					refundInfo.getRefundNo(),
					response.getBody(),
					AlipayTradeState.REFUND_ERROR.getType()); //退款失败
		}
	}
```

response.getBody()响应示例JSON:

```json
{
    "alipay_trade_refund_response": {
        "code": "10000",
        "msg": "Success",
        "buyer_logon_id": "ypv***@sandbox.com",
        "buyer_user_id": "2088622987601270",
        "fund_change": "Y",
        "gmt_refund_pay": "2022-09-04 20:34:37",
        "out_trade_no": "HSXY_ORDER_20220904144740766",
        "refund_fee": "20.00",
        "send_back_fee": "0.00",
        "trade_no": "2022090422001401270501797745"
    },
    "sign": "Ys+Q+0HfRXi+A2BKJQPgVnY0no4AvuQnZ+BvcfWd/y02JYWxF0z+FlSg+onWvgsjVRr6BeQaaUl1RwgDk4tzsBWoeUmhpA7SNu6kJBFFNA36X+fL7rF1Kvm+6pj6Z54AAFRznV+5plAwN05yjp/vfow+VMAmbYI6fESAumkfqC3KQI8J9rp0cbnW9DB9liA+/vIC1vxNxEvw52QIpiSDuMYD41o4EaCUKzzv2VzBEC5EqAQ8nO5snheYTV4piT0C9Zmfhz70sdr3LXtC/RmhTycCSsYlhmrDhGowkklx/ZsOIABYT3HQejk7Y4l//ew2s2GHb0usw3sGMIYgngbW6w=="
}
```

6.3、更新退款记录

RefundInfoService
接⼝

```java
	/**
	 * @Description 更新退款单(支付宝)
	 * @Param [refundNo, content, refundStatus] 退订号, 支付宝支付返回通知请求体, 退订状态()
	 * @return void
	 */
	void updateRefundForAliPay(String refundNo, String content, String refundStatus);
```

实现

```java
	@Override
	public void updateRefundForAliPay(String refundNo, String content, String refundStatus) {
		//将json字符串转换成Map
		Map<String, String> resultMap = new Gson().fromJson(content, HashMap.class);
		//根据退款单编号修改退款单
		LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
		//!支付宝不返回退订号,只返回订单号 ∴无法在该方法获取,只能使用传参形式
		//queryWrapper.eq(RefundInfo::getRefundNo, resultMap.get("out_refund_no"));
		queryWrapper.eq(RefundInfo::getRefundNo, refundNo);
		
		//设置要修改的字段
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setRefundStatus(refundStatus);//退款状态
		refundInfo.setContentReturn(content);//将全部响应结果存入数据库的content字段
		//更新退款单
		baseMapper.update(refundInfo, queryWrapper);
	}
```

>   支付宝不返回退订号,只返回订单号 ∴无法在该方法获取,只能使用传参形式

内置sdk.biz.info,输出日志`request.setBizModel(model);`:

![image-20220904222403255](%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904222403255.png)

### 7、统⼀收单交易退款查询

若退款接口由于网络等原因返回异常，商家可调用退款查询接口 [alipay.trade.fastpay.refund.query](https://opendocs.alipay.com/open/028sma)（统一收单交易退款查询接口）查询指定交易的退款信息。

支付宝退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。

<img src="%E5%9C%A8%E7%BA%BF%E6%94%AF%E4%BB%98%E5%AE%9E%E6%88%98.assets/image-20220904223307192.png" alt="image-20220904223307192" style="zoom:67%;" />

退款查询
（1）AliPayController

```java
	/**
	 * @Description 查询退款：测试用
	 * @Param [orderNo] 订单号 !非退款单号
	 * @return com.hsxy.paymentdemo.vo.AjaxResult
	 */
	@ApiOperation("查询退款：测试用")
	@GetMapping("/trade/fastpay/refund/{orderNo}")
	public AjaxResult queryRefund(@PathVariable String orderNo) throws Exception {
		log.info("查询退款");
		String result = aliPayService.queryRefund(orderNo);
		if (result != null){
			return AjaxResult.ok().setMessage("查询成功").data("result", result);
		}else {
			return AjaxResult.queryFailed().setMessage("查询退款失败:订单不存在");
		}
	}
```

（2）AliPayService
接⼝

```java
	/**
	 * @Description 查询退款接口调用
	 * @Param [orderNo] 订单号
	 * @return java.lang.String
	 */
	String queryRefund(String orderNo) throws AlipayApiException;
```

实现

```java
	@Override
	public String queryRefund(String orderNo) throws AlipayApiException {
		log.info("查退款单接口调用 ===> {}", orderNo);
		
		//-AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		//-AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
		JSONObject bizContent = new JSONObject();
		bizContent.put("out_trade_no", orderNo);//!商户订单号(特殊可选), 与微信不同,必须传入订单号,
												// 为方便起见(只有退全款操作)将退款请求号也设为商户订单号
		bizContent.put("out_request_no", orderNo);//!退款请求号(必填):如果在退款请求时未传入，则该值为创建交易时的商户订单号
		request.setBizContent(bizContent.toString());
		
		//- AlipayTradeQueryResponse response = alipayClient.execute(request);
		//- AlipayTradeRefundResponse response = alipayClient.execute(request);
		AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			log.info("成功, 返回结果 = " + response.getBody());
			return response.getBody();
		} else {
			log.warn("调用失败,响应码 = " + response.getCode() + ",返回结果 = " + response.getBody());
			//退款单不存在
			return null;
		}
	}
```

响应示例JSON:

```json
{
    "alipay_trade_fastpay_refund_query_response": {
        "code": "10000",
        "msg": "Success",
        "out_request_no": "HSXY_ORDER_20220904144740766",
        "out_trade_no": "HSXY_ORDER_20220904144740766",
        "refund_amount": "20.00",
        "refund_status": "REFUND_SUCCESS",
        "total_amount": "20.00",
        "trade_no": "2022090422001401270501797745"
    },
    "sign": "SrfnwPDvpPTGwGX9MuSxs71nSkr6niLM5yYmqn/4z2cZ2ZvYKbB217dlZ36N796T1xQLBCtxCC3zoGYYzCTZRqZiqr2hIwsfafWh4Mi8u8KyzpRoCBUIlndoWN9xtdl9UMVVgKL11scKzxV5/t++g8Ll+cCFlp0V+Q4+izn3l8d+BtAiyPD3i0FWDmQ0hYb9DoL/VkJZ0XRSyKkq9bheAURSRv5LpwaqBr+07327btLoUya0dYUfFRswNoofvC65TkesWze4HQ6sCQ+UIpkPTwwdwGY77Fm90z3CGVjsZc6d2YVbAKbzOtu3jk0dWlbnue66X4ihUueg42nNIX8I7A=="
}
```

### 8、收单退款冲退完成通知

alipay.trade.refund.depositback.completed

退款存在退到银⾏卡场景下时，收单会根据==银⾏回执消息==发送退款完成信息。

仅当退款发起时，在query_options中传入：deposit_back_info时会发送。

开发流程类似==⽀付结果通知==。

### 9、对账

查询对账单下载地址接⼝
（1）AliPayController


```java
	@ApiOperation("下载账单")
	@GetMapping("/bill/downloadurl/query/{billDate}/{type}")
	public AjaxResult downloadBill(@PathVariable String billDate, @PathVariable String type) throws Exception {
		log.info("下载账单");
		String result = aliPayService.downloadBill(billDate, type);
		return AjaxResult.ok().setMessage("获取账单url成功").data("result", result);
	}
```


（2）AliPayService
接⼝

```java
	/**
	 * @Description 下载账单
	 * @Param [billDate] 订单日期(无法查询当日/月账单) 格式:2022-08-29(日账单) / 2022-08(月账单)
	 * @Param [type] 查询账单类型 选择:{交易账单(业务账单) trade,资金账单(账务账单) signcustomer}
	 * @return java.lang.String
	 */
	String downloadBill(String billDate, String type) throws AlipayApiException;
```

实现

```java
	@Override
	public String downloadBill(String billDate, String type) throws AlipayApiException {
		log.warn("下载账单接口调用,日期: {},账单类型: {}", billDate, type);
		//获取账单url地址
		AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
		JSONObject bizContent = new JSONObject();
		bizContent.put("bill_type", type);
		bizContent.put("bill_date", billDate);
		request.setBizContent(bizContent.toString());
		AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
		
		if(response.isSuccess()){
			log.info("调用成功，返回结果 ===> " + response.getBody());
			//获取账单下载地址
			HashMap<String, LinkedTreeMap> resultMap = new Gson().fromJson(response.getBody(), HashMap.class);
			LinkedTreeMap billDownloadurlResponse = resultMap.get("alipay_data_dataservice_bill_downloadurl_query_response");
			String billDownloadUrl = (String)billDownloadurlResponse.get("bill_download_url");
			return billDownloadUrl;
		} else {
			log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
			throw new RuntimeException("申请账单失败");
		}
	}
```















