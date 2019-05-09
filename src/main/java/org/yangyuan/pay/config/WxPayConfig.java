package org.yangyuan.pay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxPayConfig {

    @Value("${pay.wx.appid:}")
    private String _appid;
    @Value("${pay.wx.mchid:}")
    private String _mchid;
    @Value("${pay.wx.key:}")
    private String _key;
    @Value("${pay.wx.notifyURL:}")
    private String _notifyURL;
    @Value("${pay.wx.certPath:}")
    private String _certPath;
    @Value("${pay.wx.certPassword:}")
    private String _certPassword;

    /**
     * 公众账号ID
     */
    private static String appid;
    /**
     * 商户号
     */
    private static String mchid;
    /**
     * 合作key
     */
    private static String key;
    /**
     * 支付回调地址
     */
    private static String notifyURL;
    /**
     * 证书绝对路径
     */
    private static String certPath;
    /**
     * 证书密码
     */
    private static String certPassword;
    /**
     * 统一下单接口链接
     */
    private static String unifiedorderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**
     * 企业付款接口链接
     */
    private static String enterprisePayURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    /**
     * 单笔订单查询接口
     */
    private static String orderQueryURL = "https://api.mch.weixin.qq.com/pay/orderquery";
    /**
     * 申请退款接口
     */
    private static String orderRefundURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    /**
     * 退款查询接口
     */
    private static String orderRefundQueryURL = "https://api.mch.weixin.qq.com/pay/refundquery";
    /**
     * 关闭订单接口
     */
    private static String closeOrderURL = "https://api.mch.weixin.qq.com/pay/closeorder";
    /**
     * 转账接口
     */
    private static String transfersURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    /**
     * 转账查询接口
     */
    private static String transfersQueryURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";

    @Bean
    public Object wxPayConfigInjector(){
        appid = _appid;
        mchid = _mchid;
        key = _key;
        notifyURL = _notifyURL;
        certPath = _certPath;
        certPassword = _certPassword;

        return new Object();
    }

    public static String getAppid() {
        return appid;
    }

    public static String getMchid() {
        return mchid;
    }

    public static String getKey() {
        return key;
    }

    public static String getNotifyURL() {
        return notifyURL;
    }

    public static String getUnifiedorderURL() {
        return unifiedorderURL;
    }

    public static String getEnterprisePayURL() {
        return enterprisePayURL;
    }

    public static String getOrderQueryURL() {
        return orderQueryURL;
    }

    public static String getOrderRefundURL() {
        return orderRefundURL;
    }

    public static String getOrderRefundQueryURL() {
        return orderRefundQueryURL;
    }

    public static String getCloseOrderURL() {
        return closeOrderURL;
    }

    public static String getTransfersURL() {
        return transfersURL;
    }

    public static String getTransfersQueryURL() {
        return transfersQueryURL;
    }

    public static String getCertPath() {
        return certPath;
    }

    public static String getCertPassword() {
        return certPassword;
    }
}
