package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.Trade;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.util.PayMD5;

import java.util.UUID;

/**
 * 微信支付订单
 * @author yangyuan
 * @date 2017年5月4日
 */
public class WxTrade implements Trade {
    private final String appid;
    private final String mchid;
    private final String deviceInfo;
    private final String nonceStr;
    private final String body;
    private final String detail;
    private final String attach;
    private final String outTradeNo;
    private final String feeType;
    private final String totalFee;
    private final String spbillCreateIp;
    private final String timeStart;
    private final String timeExpire;
    private final String goodsTag;
    private final String notifyUrl;
    private final String tradeType;
    private final String productId;
    private final String limitPay;
    private final String sceneInfo;
    private final String openid;

    private WxTrade(Builder builder){
        this.appid = builder.appid;
        this.mchid = builder.mchid;
        this.deviceInfo = builder.deviceInfo;
        this.nonceStr = builder.nonceStr;
        this.body = builder.body;
        this.detail = builder.detail;
        this.attach = builder.attach;
        this.outTradeNo = builder.outTradeNo;
        this.feeType = builder.feeType;
        this.totalFee = builder.totalFee;
        this.spbillCreateIp = builder.spbillCreateIp;
        this.timeStart = builder.timeStart;
        this.timeExpire = builder.timeExpire;
        this.goodsTag = builder.goodsTag;
        this.notifyUrl = builder.notifyUrl;
        this.tradeType = builder.tradeType;
        this.productId = builder.productId;
        this.limitPay = builder.limitPay;
        this.sceneInfo = builder.sceneInfo;
        this.openid = builder.openid;
    }
    
    public String getAppid() {
        return appid;
    }

    public String getMchid() {
        return mchid;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getBody() {
        return body;
    }

    public String getDetail() {
        return detail;
    }

    public String getAttach() {
        return attach;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public String getFeeType() {
        return feeType;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getProductId() {
        return productId;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public String getSceneInfo() {
        return sceneInfo;
    }

    public String getOpenid() {
        return openid;
    }

    /**
     * 基础构造器
     * @return builder
     */
    public static Builder basic(){
        Builder builder = new Builder();
        
        return builder.appid(WxPayConfig.getAppid())
                        .mchid(WxPayConfig.getMchid())
                        .notifyUrl(WxPayConfig.getNotifyURL())
                        .nonceStr(PayMD5.encode(UUID.randomUUID().toString()));
    }
    
    /**
     * 移动APP支付构造器
     * @return builder
     */
    public static Builder mobilePay(){
        Builder builder = basic();
        
        return builder.tradeType("APP");
    }
    
    /**
     * 电脑网页支付构造器
     * @return builder
     */
    public static Builder webPcPay(){
        Builder builder = basic();
        
        return builder.tradeType("NATIVE");
    }

    /**
     * 手机网页支付构造器
     * @return builder
     */
    public static Builder webMobilePay(){
        Builder builder = basic();

        return builder.tradeType("MWEB");
    }

    /**
     * 扫码支付构造器
     * @return builder
     */
    public static Builder qrcodePay(){
        Builder builder = basic();

        return builder.tradeType("NATIVE");
    }

    /**
     * JS-SDK支付构造器
     * @return builder
     */
    public static Builder jsSdkPay(){
        Builder builder = basic();

        return builder.tradeType("JSAPI");
    }

    public static class Builder{
        /**
         * 应用ID
         * <p><b>example:</b></p>
         * <p>wxd678efh567hg6787</p>
         */
        private String appid;
        /**
         * 商户号
         * <p><b>example:</b></p>
         * <p>1230000109</p>
         */
        private String mchid;
        /**
         * 设备号(可空)
         * <p><b>example:</b></p>
         * <p>013467007045764</p>
         */
        private String deviceInfo;
        /**
         * 随机字符串
         * <p><b>example:</b></p>
         * <p>5K8264ILTKCH16CQ2502SI8ZNMTM67VS</p>
         */
        private String nonceStr;
        /**
         * 商品描述
         * <p><b>example:</b></p>
         * <p>腾讯充值中心-QQ会员充值</p>
         */
        private String body;
        /**
         * 商品详情(可空)
         * <p><b>example:</b></p>
         * <p>内容过长，请参考微信官方文档</p>
         */
        private String detail;
        /**
         * 附加数据(可空)，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
         * <p><b>example:</b></p>
         * <p>深圳分店</p>
         */
        private String attach;
        /**
         * 商户订单号，商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
         * <p><b>example:</b></p>
         * <p>20150806125346</p>
         */
        private String outTradeNo;
        /**
         * 货币类型(可空)
         * <p><b>example:</b></p>
         * <p>CNY</p>
         */
        private String feeType = "CNY";
        /**
         * 总金额，单位为分
         * <p><b>example:</b></p>
         * <p>888</p>
         */
        private String totalFee;
        /**
         * 终端IP，用户端实际ip
         * <p><b>example:</b></p>
         * <p>123.12.12.123</p>
         */
        private String spbillCreateIp;
        /**
         * 交易起始时间(可空)
         * <p><b>example:</b></p>
         * <p>20091225091010</p>
         */
        private String timeStart;
        /**
         * 交易结束时间(可空)
         * <p><b>example:</b></p>
         * <p>20091227091010</p>
         */
        private String timeExpire;
        /**
         * 商品标记(可空)
         * <p><b>example:</b></p>
         * <p>WXG</p>
         */
        private String goodsTag;
        /**
         * 通知地址，接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数
         * <p><b>example:</b></p>
         * <p>http://www.weixin.qq.com/wxpay/pay.php</p>
         */
        private String notifyUrl;
        /**
         * 交易类型
         * <p><b>example:</b></p>
         * <p>APP</p>
         */
        private String tradeType;
        /**
         * 商品ID<br>trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义
         * <p><b>example:</b></p>
         * <p>12235413214070356458058</p>
         */
        private String productId;
        /**
         * 指定支付方式(可空)
         * <p><b>example:</b></p>
         * <p>no_credit</p>
         */
        private String limitPay;
        /**
         * 场景信息
         * <p><b>example:</b></p>
         * <p>no_credit</p>
         */
        private String sceneInfo;
        /**
         * 用户标识
         * <p><b>example:</b></p>
         * <p>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</p>
         */
        private String openid;

        public Builder appid(String appid) {
            this.appid = appid;
            return this;
        }
        public Builder mchid(String mchid) {
            this.mchid = mchid;
            return this;
        }
        public Builder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }
        public Builder nonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }
        public Builder attach(String attach) {
            this.attach = attach;
            return this;
        }
        public Builder outTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
            return this;
        }
        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }
        public Builder feeType(String feeType) {
            this.feeType = feeType;
            return this;
        }
        public Builder totalFee(String totalFee) {
            this.totalFee = totalFee;
            return this;
        }
        public Builder spbillCreateIp(String spbillCreateIp) {
            this.spbillCreateIp = spbillCreateIp;
            return this;
        }
        public Builder timeStart(String timeStart) {
            this.timeStart = timeStart;
            return this;
        }
        public Builder timeExpire(String timeExpire) {
            this.timeExpire = timeExpire;
            return this;
        }
        public Builder goodsTag(String goodsTag) {
            this.goodsTag = goodsTag;
            return this;
        }
        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }
        public Builder tradeType(String tradeType) {
            this.tradeType = tradeType;
            return this;
        }
        public Builder limitPay(String limitPay) {
            this.limitPay = limitPay;
            return this;
        }
        public Builder sceneInfo(String sceneInfo) {
            this.sceneInfo = sceneInfo;
            return this;
        }
        public Builder openid(String openid) {
            this.openid = openid;
            return this;
        }

        public WxTrade build(){
            return new WxTrade(this);
        }
    }
}
