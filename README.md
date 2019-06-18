## 关于

使用本模块，可轻松实现支付宝支付、微信支付对接，从而专注于业务，无需关心第三方逻辑。

模块完全独立，无支付宝、微信SDK依赖。

基于Spring Boot。

依赖Redis。



## 我能做什么

**支付宝：**电脑网站支付、手机网站支付、扫码支付、APP支付。

**微信：**电脑网站支付(同扫码支付)、手机网站支付(微信外H5支付)、扫码支付、APP支付、JSAPI支付(微信内H5支付)。

统一支付方法。

异步回调封装。

订单状态查询。

退款。

公对私转账。

*请确保支付宝、微信帐号已经申请了相应业务、权限*



## 模块集成

只需要简单的、非侵入式的配置，即可集成到项目中。



> 添加模块到Maven项目中

父项目中添加`pay-spring-boot`模块依赖(pom.xml)：

```
<modules>
    ...
    <module>pay-spring-boot</module>
    ...
</modules>
```



修改`pay-spring-boot`的父项目(pom.xml)：

```
<parent>
    <groupId>yourself parent groupId</groupId>
    <artifactId>yourself parent artifactId</artifactId>
    <version>yourself parent version</version>
</parent>
```



> 支付凭证

在`application.yml`(或`application-*.yml`，视项目具体情况而定)中添加如下配置：

```
pay:
  wx:
    appid: wx1d96c6yxxc0d192a
    mchid: 1519719912
    key: A6nvI8Xp6A6nvI8Xp6A6nvI8Xp6
    notifyURL: https://xxx.com/wxpay/notify
    certPath: /data/cert/wx/apiclient_cert.p12
    certPassword: 1517923901
  ali:
    appid: 2019138363328891
    privateKey: MIIEuwIBADANBgkqhkiG9w...
    notifyURL: https://xxx.com/alipay/notify
```

关于配置项的具体含义参考`AliPayConfig`、`WxPayConfig`两个类，里边有详细说明。



> 注入Redis连接池

在项目中创建一个Redis连接池工厂实现类，名称无所谓，必须实现`RedisResourceFactory`接口，然后添加`@ResourceFactoryComponent`注解，例如：

```
@ResourceFactoryComponent
public class DefaultRedisResourceFactory implements RedisResourceFactory {
    @Override
    public JedisPool getJedisPool() {
    	/*
    	  框架不关心JedisPool是怎么来的
    	  这里的RedisService是我自己实现的服务
    	  根据项目实际情况换成你自己的实现
    	*/
        return RedisService.getPool();
    }
}
```



> 配置Spring扫描包路径

由于`pay-spring-boot`模块与你的项目不在同一个包名下，`Spring`默认不会扫描`pay-spring-boot`模块，因此必须手动配置。

在`Spring Boot`项目入口类中，找到`@SpringBootApplication`注解，添加`scanBasePackages`参数，其中`com.yourself`是你自己的项目包路径，根据实际情况修改，`org.yangyuan`为`pay-spring-boot`模块包路径，无需改动。例如：

```
@SpringBootApplication(scanBasePackages={"com.yourself", "org.yangyuan"})
public class AdminApplication {
  public static void main(String[] args) {
    SpringApplication.run(AdminApplication.class, args);
  }
}
```



## 如何支付

一般来说，项目中会有不同的支付场景，比如：购买商品、充值等支付业务。

现在用商品购买举例，通过这个例子展示如何使用框架。



> 创建支付适配器

支付适配器是支付模块和数据访问层的桥梁，适配器将支付结果抽象成**支付成功(doPaySuccess)**、**支付失败(doPayFail)**、**退款成功(doRefundSuccess)**、**退款失败(doRefundFail)**四种情况，代表了四个状态。

建议不同的场景使用不同的适配器，不要所有业务逻辑都放一起。

创建订单的操作，也建议放在适配器中实现。

以下是商品适配器例子：

```
@Service
public class GoodsTradeService extends AbstractPayAdaptor {
    
    @Autowired
    private GoodsTradeManager goodsTradeManager;

    /**
     * 创建订单
     * @param id 商品id
     * @return
     */
    public Message createOrder(long id){
        
    }

    @Override
    public void doPaySuccess(String outTradeNo) {
        //支付成功，这里一般要更新数据库的状态
    }

    @Override
    public void doPayFail(String outTradeNo) {
        //支付失败，这里一般要更新数据库的状态
    }

    @Override
    public void doRefundSuccess(String outTradeNo) {
        //退款成功，这里一般要更新数据库的状态
    }

    @Override
    public void doRefundFail(String outTradeNo) {
        //退款失败，这里一般要更新数据库的状态
    }
}
```

看起来非常简单，继承`AbstractPayAdaptor`抽象类，然后通过`@Service`注解交给Spring管理，为什么要交给Spring呢？因为这里你需要注入数据访问层的实例(Dao)，不然怎么操作数据库，只不过我这没有写而已~

这里有一个`GoodsTradeManager`，是接下来要介绍的支付管理器，先不管它。

仔细观察会发现，示例中的适配器名称叫`GoodsTradeService`，为什么我不管他叫`GoodsTradePayAdaptor`呢？从支付框架的角度看，这的确是一个适配器实现，但从业务的角度看，它是商品订单的服务中心，不仅要处理订单状态，还要承担创建订单的职责，它底层(数据库层)关联的本来就是一个订单表，把它称作订单服务，更加容易理解。

因此，所谓的适配器，就是用来适配支付框架和数据访问层的。



> 创建支付管理器

有了适配器，就有了数据访问的能力，再配上一个管理器作为统一调度中心，那么支付这事就搞定了，实现一个管理器非常容易：

```
@PayManagerComponent
public class GoodsTradeManager extends AbstractPayManager {

    @Autowired
    private GoodsTradeService goodsTradeService;

    @Override
    public String getTradeType() {
        return "0";
    }

    @Override
    public AbstractPayAdaptor getPayAdaptor() {
        return goodsTradeService;
    }
}
```

首先继承`AbstractPayManager`，然后使用`@PayManagerComponent`注解注册管理器，这没什么神奇的，只不过是告诉框架这里有一个管理器，并且把这个管理器交给Spring维护。

`getTradeType`方法返回**长度为1的字符串，建议取值范围[0-9a-z]**，类型会拼接到订单号中，所以不建议使用特殊字符。因此，一个项目中最多可创建36个不同的管理器。

`getPayAdaptor`方法返回上一步创建的适配器，管理器中包含了适配器。

因此，所谓的管理器，管理的目标就是适配器，同时担负起统一支付调度的重任，管理器是支付模块的窗口。

最佳实践是：**每对管理器-适配器对应一种支付业务**。



> 发起支付

接下来就可以发起支付了，非常简单，先来看一个支付宝扫码支付示例：

```
Trade trade = AliTrade
                .qrcodePay()
                .subject("商品标题")
                .body("商品描述")
                .outTradeNo(goodsTradeManager.newTradeNo("你自己的用户唯一标识"))
                .totalAmount("0.01")
                .build();
TradeToken<String> token = goodsTradeManager.qrcodePay(trade);
String url = token.value();
```

先通过`AliTrade`构造器的`qrcodePay`方法创建一个扫码支付订单，然后调用管理器的`qrcodePay`方法生成订单凭证，不同的支付产品的订单凭证可能不同，你可以自由选择泛型，扫码支付的凭证就是一个url链接，因此我使用的String类型，调用凭证的`value`方法，即可获得凭证内容，凭证内容直接返回给前端(网页或APP)，前端即可调起支付。

`goodsTradeManager`上一步已经创建好，直接`@Autowired`注入即可。

`newTradeNo`方法非常重要，它可以帮你生成一个订单号，也就是商户订单号，在我的设计中，为了省去繁琐的全局唯一订单号生成，将订单号和用户关联起来，规避了订单号唯一性问题，用户唯一标识根据你的系统自由选择，建议长度在**[6-10]**之间，并且为**固定长度**，不能使用特殊字符，用户唯一标识会直接拼接到订单号中，长度不固定或太长的话，订单号会非常难看，不规范，如需更多了解，直接看代码注释。

`AliTrade`构造器所有的属性均与支付宝官方文档相对应，具体含义参考代码注释或者支付宝官方文档。

订单的类型`AliTrade.qrcodePay`和管理器方法`goodsTradeManager.qrcodePay`必须配套使用。



再来看一个微信H5支付的例子：

```
Trade trade = WxTrade
                .webMobilePay()
                .body("商品标题")
                .outTradeNo(goodsTradeManager.newTradeNo("你自己的用户唯一标识"))
                .totalFee("1")
                .spbillCreateIp("127.0.0.1")
                .sceneInfo("商品测试场景")
                .build();
TradeToken<String> token = goodsTradeManager.webMobilePay(trade);
String url = token.value();
```

只不过是把`AliTrade`换成了`WxTrade`，然后调用`WxTrade.webMobilePay`构造器，加上配套的`goodsTradeManager.webMobilePay`即可完成微信H5支付。

微信H5支付的凭证也是一个url，直接交给前端处理即可。

由此可以看出，我们只需要关心订单构造器，将订单构造好，直接调用管理器对应的方法即可，管理器不关心支付宝还是微信，只需要接收一个配套的订单，最后拿到订单凭证，就算是完工了。

依此类推，即可完成其它类型的支付业务。



> 异步回调

涉及钱的事没有小事，别忘了还有支付结果**异步回调**。

前端的支付结果回调是**同步回调**，仅供参考，**必须以后端的结果为准**。

*支付宝回调：*

```
@PostMapping(value = "/notify")
public void notify(HttpServletRequest request, HttpServletResponse response){
    /*
        解析请求参数
     */
    Map<String, String> params = NoticeManagers.getDefaultManager().receiveAliParams(request);

    /*
        封装
     */
    AliPayNoticeInfo info = new AliPayNoticeInfo();
    TradeStatus status = NoticeManagers.getDefaultManager().execute(params, info);

    /*
        持久化回调数据
     */
    //TODO: 强烈建议将AliPayNoticeInfo持久化到数据库中，以备不时之需，当然你也可以忽略

    /*
        业务分发
     */
    AbstractPayManager payManager = (AbstractPayManager) PayManagers.find(status.getTradeNo());
    payManager.doTradeStatus(status);

    /*
        响应
     */
    NoticeManagers.getDefaultManager().sendAliResponse(response);
}
```

*微信回调：*

```
@PostMapping(value = "/notify")
public void notify(HttpServletRequest request, HttpServletResponse response){
    /*
        解析请求参数
     */
    Map<String, String> params = NoticeManagers.getDefaultManager().receiveWxParams(request);

    /*
        封装
     */
    WxPayNoticeInfo info = new WxPayNoticeInfo();
    TradeStatus status = NoticeManagers.getDefaultManager().execute(params, info);

    /*
        持久化回调数据
     */
    //TODO: 强烈建议将WxPayNoticeInfo持久化到数据库中，以备不时之需，当然你也可以忽略

    /*
        业务分发
     */
    AbstractPayManager payManager = (AbstractPayManager) PayManagers.find(status.getTradeNo());
    payManager.doTradeStatus(status);

    /*
        响应
     */
    NoticeManagers.getDefaultManager().sendWxResponse(response);
}
```

最基本的Spring MVC Controller代码不用我教了吧。

定义一个控制器，接收HTTP请求、响应对象，通过框架解析出参数和订单状态，然后将订单状态分发给适配器，实现订单状态更新，最后给支付宝、微信一个响应，告诉他们已经接收到请求。

回调处理非常规范化，基本不需要做什么改动(直接Copy)，唯一需要做的，也是非常重要的，就是根据你自己项目的实际情况，以恰当的方式持久化回调数据。

这里的`@PostMapping`请求路径，就是配置在`application.yml`中的`notifyURL`，必须保证公网可以无障碍访问。

**注意：支付宝、微信服务器会并发请求回调接口(一般为1~3次)，必须保证幂等性！**



> 主动同步订单状态

用来弥补特殊原因造成的异步回调丢失，**异步回调不是100%可靠的**。

由于需要请求支付宝、微信服务器，所以速度较慢。

*支付宝订单：*

```
Trade trade = AliTrade.query().outTradeNo("商户订单号").build();
TradeStatus status = goodsTradeManager.status(trade);
status.isPaySuccess(); //是否支付成功，其它状态不一一列举，自行看代码
```

*微信订单：*

```
Trade trade = WxTrade.basic().outTradeNo("商户订单号").build();
TradeStatus status = goodsTradeManager.status(trade);
status.isPaySuccess(); //是否支付成功，其它状态不一一列举，自行看代码
```



## 如何转账

> 公对私转账

由公司帐号向个人帐号转账。

*支付宝转账：*

```
/*
    构造转账订单
 */
AliTransferTrade transferTrade = AliTransferTrade
                                        .transfer()
                                        .outBizNo("商户转账唯一订单号")
                                        .payeeAccount("收款人支付宝帐号")
                                        .amount("0.01")
                                        .build();

/*
    转账
 */
try{
    AliTransfer.getInstance().transfer(transferTrade);
}catch (TransferException e){
    // 转账失败处理逻辑...
}
```

转账方法无返回值，不发生异常代表转账成功，发生异常代表转账失败，自行处理。

订单参数含义参考支付宝官方文档或代码注释。



*微信转账：*

```
/*
    构造转账订单
 */
WxTransferTrade transferTrade = WxTransferTrade
                                        .transfer()
                                        .partnerTradeNo("商户转账唯一订单号")
                                        .openid("收款人openid")
                                        .amount("1")
                                        .spbillCreateIp("127.0.0.1")  //这里是调用接口的服务器公网IP，自行获取
                                        .build();
/*
    转账
 */
try{
    WxTransfer.getInstance().transfer(transferTrade);
}catch (TransferException e){
    // 转账失败处理逻辑...
}
```

转账方法无返回值，不发生异常代表转账成功，发生异常代表转账失败，自行处理。

订单参数含义参考微信官方文档或代码注释。



> 转账状态查询

*支付宝：*

```
/*
    构造转账查询订单
 */
AliTransferTrade transferTrade = AliTransferTrade
                                        .query()
                                        .outBizNo("商户转账唯一订单号")
                                        .build();
/*
    转账查询
 */
TransferStatus status = AliTransfer.getInstance().status(transferTrade);;
status.isSuccess();  //转账成功，其他状态自行查看代码，不一一列举
```



*微信：*

```
/*
    构造转账查询订单
 */
WxTransferTrade transferTrade = WxTransferTrade
                                        .custom()
                                        .partnerTradeNo("商户转账唯一订单号")
                                        .build();
/*
    转账查询
 */
TransferStatus status = WxTransfer.getInstance().status(transferTrade);;
status.isSuccess();  //转账成功，其他状态自行查看代码，不一一列举
```



## 附加工具

> 获取客户端IP地址

微信支付大部分场景需要客户端IP地址，可以通过本模块`PayHttpUtil.getRealClientIp`方法获取。

如果获取不到，请检查代理软件是否正确设置了`X-Forwarded-For`。



## 其他

如有疑问，欢迎积极反馈，直接提`Issues`别客气。







