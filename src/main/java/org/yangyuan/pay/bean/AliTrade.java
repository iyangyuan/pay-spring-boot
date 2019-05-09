package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.Trade;
import org.yangyuan.pay.config.AliPayConfig;
import org.yangyuan.pay.config.CommonConfig;

/**
 * 支付宝订单
 * @author yangyuan
 * @date 2017年4月28日
 */
public class AliTrade implements Trade {
    private final String appId;
    private final String method;
    private final String format;
    private final String charset;
    private final String timestamp;
    private final String version;
    private final String notifyUrl;
    private final String returnUrl;
    private final String body;
    private final String subject;
    private final String outTradeNo;
    private final String timeoutExpress;
    private final String totalAmount;
    private final String productCode;
    private final String goodsType;
    private final String passbackParams;
    private final String storeId;
    private final String quitUrl;
    private final String qrPayMode;
    private final String qrcodeWidth;
    private final String refundAmount;
    private final String refundReason;
    private final String outRequestNo;
    
    private AliTrade(Builder builder){
        this.appId = builder.appId;
        this.method = builder.method;
        this.format = builder.format;
        this.charset = builder.charset;
        this.timestamp = builder.timestamp;
        this.version = builder.version;
        this.notifyUrl = builder.notifyUrl;
        this.returnUrl = builder.returnUrl;
        this.body = builder.body;
        this.subject = builder.subject;
        this.outTradeNo = builder.outTradeNo;
        this.timeoutExpress = builder.timeoutExpress;
        this.totalAmount = builder.totalAmount;
        this.productCode = builder.productCode;
        this.goodsType = builder.goodsType;
        this.passbackParams = builder.passbackParams;
        this.storeId = builder.storeId;
        this.quitUrl = builder.quitUrl;
        this.qrPayMode = builder.qrPayMode;
        this.qrcodeWidth = builder.qrcodeWidth;
        this.refundAmount = builder.refundAmount;
        this.refundReason = builder.refundReason;
        this.outRequestNo = builder.outRequestNo;
    }

    public String getAppId() {
        return appId;
    }

    public String getMethod() {
        return method;
    }

    public String getFormat() {
        return format;
    }

    public String getCharset() {
        return charset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public String getTimeoutExpress() {
        return timeoutExpress;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public String getPassbackParams() {
        return passbackParams;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getQuitUrl() {
        return quitUrl;
    }

    public String getQrPayMode() {
        return qrPayMode;
    }

    public String getQrcodeWidth() {
        return qrcodeWidth;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public String getOutRequestNo() {
        return outRequestNo;
    }

    /**
     * 基础构造器
     * @return builder
     */
    public static Builder basic(){
        Builder builder = new Builder();
        
        return builder.appId(AliPayConfig.getAppid())
                      .notifyUrl(AliPayConfig.getNotifyURL());
    }
    
    /**
     * 移动支付构造器
     * @return builder
     */
    public static Builder mobilePay(){
        return basic().method("alipay.trade.app.pay")
                      .productCode("QUICK_MSECURITY_PAY");
    }
    
    /**
     * 手机网页支付构造器
     * @return builder
     */
    public static Builder webMobilePay(){
        return basic().method("alipay.trade.wap.pay")
                      .productCode("QUICK_WAP_WAY");
    }
    
    /**
     * 电脑网页支付构造器
     * @return builder
     */
    public static Builder webPcPay(){
        return basic().method("alipay.trade.page.pay")
                      .productCode("FAST_INSTANT_TRADE_PAY")
                      .qrPayMode("2");
    }

    /**
     * 扫码支付
     * @return builder
     */
    public static Builder qrcodePay(){
        return basic().method("alipay.trade.page.pay")
                      .productCode("FAST_INSTANT_TRADE_PAY")
                      .qrPayMode("4")
                      .qrcodeWidth("200");
    }

    /**
     * 订单查询构造器
     * @return builder
     */
    public static Builder query(){
        return basic().method("alipay.trade.query")
                      .notifyUrl("");
    }
    
    /**
     * 退款构造器
     * @return builder
     */
    public static Builder refund(){
        return basic().method("alipay.trade.refund")
                      .notifyUrl("");
    }
    
    /**
     * 构造器
     * @author yangyuan
     * @date 2017年4月28日
     */
    public static class Builder{

        /*
            公共参数
         */

        /**
         * 支付宝分配给开发者的应用ID
         * <p><b>example:</b></p>
         * <p>2014072300007148</p>
         */
        private String appId;
        /**
         * 接口名称
         * <p><b>example:</b></p>
         * <p>alipay.trade.query</p>
         */
        private String method;
        /**
         * 响应数据格式，仅支持JSON
         * <p><b>example:</b></p>
         * <p>JSON</p>
         */
        private String format = "JSON";
        /**
         * 请求使用的编码格式，如utf-8,gbk,gb2312等
         * <p><b>example:</b></p>
         * <p>UTF-8</p>
         */
        private String charset = CommonConfig.UNIFY_CHARSET;
        /**
         * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
         * <p><b>example:</b></p>
         * <p>2014-07-24 03:07:50</p>
         */
        private String timestamp;
        /**
         * 调用的接口版本，固定为：1.0
         * <p><b>example:</b></p>
         * <p>1.0</p>
         */
        private String version = "1.0";
        /**
         * 服务器异步通知页面路径
         * <p><b>example:</b></p>
         * <p>http://notify.msp.hk/notify.htm</p>
         */
        private String notifyUrl;
        /**
         * 同步返回地址，HTTP/HTTPS开头字符串
         * <p><b>example:</b></p>
         * <p>https://m.alipay.com/Gk8NF23</p>
         */
        private String returnUrl;

        /*
            业务参数
         */

        /**
         * 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
         * <p><b>example:</b></p>
         * <p>Iphone6 16G</p>
         */
        private String body;
        /**
         * 商品的标题/交易标题/订单标题/订单关键字等
         * <p><b>example:</b></p>
         * <p>大乐透</p>
         */
        private String subject;
        /**
         * 商户网站唯一订单号
         * <p><b>example:</b></p>
         * <p>70501111111S001111119</p>
         */
        private String outTradeNo;
        /**
         * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
         * 注：若为空，则默认为15d。
         * <p><b>example:</b></p>
         * <p>90m</p>
         */
        private String timeoutExpress;
        /**
         * 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
         * <p><b>example:</b></p>
         * <p>9.00</p>
         */
        private String totalAmount;
        /**
         * 销售产品码，商家和支付宝签约的产品码，不同的产品有不同的固定值
         * <p><b>example:</b></p>
         * <p>QUICK_MSECURITY_PAY</p>
         */
        private String productCode;
        /**
         * 商品主类型：0—虚拟类商品，1—实物类商品
         * 注：虚拟类商品不支持使用花呗渠道
         * <p><b>example:</b></p>
         * <p>0</p>
         */
        private String goodsType = "0";
        /**
         * 公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
         * <p><b>example:</b></p>
         * <p>merchantBizType%3d3C%26merchantBizNo%3d2016010101111</p>
         */
        private String passbackParams;
        /**
         * 商户门店编号。该参数用于请求参数中以区分各门店，非必传项。
         * <p><b>example:</b></p>
         * <p>NJ_001</p>
         */
        private String storeId;
        /**
         * 添加该参数后在h5支付收银台会出现返回按钮，可用于用户付款中途退出并返回到该参数指定的商户网站地址。注：该参数对支付宝钱包标准收银台下的跳转不生效。
         * <p><b>example:</b></p>
         * <p>http://www.taobao.com/product/113714.html</p>
         */
        private String quitUrl;
        /**
         * PC扫码支付的方式，支持前置模式和跳转模式。
         * 前置模式是将二维码前置到商户的订单确认页的模式。需要商户在自己的页面中以iframe方式请求支付宝页面。具体分为以下几种：
         * 0：订单码-简约前置模式，对应iframe宽度不能小于600px，高度不能小于300px；
         * 1：订单码-前置模式，对应iframe宽度不能小于300px，高度不能小于600px；
         * 3：订单码-迷你前置模式，对应iframe宽度不能小于75px，高度不能小于75px；
         * 4：订单码-可定义宽度的嵌入式二维码，商户可根据需要设定二维码的大小。
         *
         * 跳转模式下，用户的扫码界面是由支付宝生成的，不在商户的域名下。
         * 2：订单码-跳转模式
         * <p><b>example:</b></p>
         * <p>4</p>
         */
        private String qrPayMode;
        /**
         * 商户自定义二维码宽度
         * 注：qr_pay_mode=4时该参数生效
         * <p><b>example:</b></p>
         * <p>100</p>
         */
        private String qrcodeWidth;

        /*
            退款相关
         */

        /**
         * 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
         * <p><b>example:</b></p>
         * <p>200.12</p>
         */
        private String refundAmount;

        /**
         * 退款的原因说明
         * <p><b>example:</b></p>
         * <p>正常退款</p>
         */
        private String refundReason;

        /**
         * 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
         * <p><b>example:</b></p>
         * <p>HZ01RF001</p>
         */
        private String outRequestNo;

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }

        public Builder returnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder outTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
            return this;
        }

        public Builder timeoutExpress(String timeoutExpress) {
            this.timeoutExpress = timeoutExpress;
            return this;
        }

        public Builder totalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder productCode(String productCode) {
            this.productCode = productCode;
            return this;
        }

        public Builder goodsType(String goodsType) {
            this.goodsType = goodsType;
            return this;
        }

        public Builder passbackParams(String passbackParams) {
            this.passbackParams = passbackParams;
            return this;
        }

        public Builder storeId(String storeId) {
            this.storeId = storeId;
            return this;
        }

        public Builder quitUrl(String quitUrl) {
            this.quitUrl = quitUrl;
            return this;
        }

        public Builder qrPayMode(String qrPayMode) {
            this.qrPayMode = qrPayMode;
            return this;
        }

        public Builder qrcodeWidth(String qrcodeWidth) {
            this.qrcodeWidth = qrcodeWidth;
            return this;
        }

        public Builder refundAmount(String refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        public Builder refundReason(String refundReason) {
            this.refundReason = refundReason;
            return this;
        }

        public Builder outRequestNo(String outRequestNo) {
            this.outRequestNo = outRequestNo;
            return this;
        }

        public AliTrade build(){
            return new AliTrade(this);
        }
    }
}
