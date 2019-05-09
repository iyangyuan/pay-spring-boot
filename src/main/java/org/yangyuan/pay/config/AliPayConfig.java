package org.yangyuan.pay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付全局统一配置
 * @author yangyuan
 * @date 2017年4月28日
 */
@Configuration
public class AliPayConfig {

    @Value("${pay.ali.appid:}")
    private String _appid;
    @Value("${pay.ali.privateKey:}")
    private String _privateKey;
    @Value("${pay.ali.notifyURL:}")
    private String _notifyURL;

    /**
     * 应用ID
     */
    private static String appid;
    /**
     * 商户的私钥
     */
    private static String privateKey;
    /**
     * 支付回调地址
     */
    private static String notifyURL;
    /**
     * 支付宝网关
     */
    private static String gateway = "https://openapi.alipay.com/gateway.do";
    /**
     * 支付宝的公钥
     */
    private static String aliPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvsH3SyXNZNIcMiwgvzJWXvocYZwN0XHSq1ix06maJt8aJ6tTl+iG6vEkhQzIpsnZOyNW3aVaE3jEmRE51kKR81pO3Ulj3wg2wE6DufGmTobDclrjZlEJgCjINC+p10YEMo83CWhlPhtTmcY3cGqRpfBsacHXtU4vMwsR1WH3amAwKaPZVHqX0WH+mwMUW41UU1NFwOB5LqbmtnupEMwQ77M6DsxtTTXG4laQReQIvLkg8PGEm9U0jfqvLqfxFNj701BjR2BwN22jPJtCZji/tYWlUUVS2EtZVbvu8tj7hcRGyiQBvLNNhsiNfVH5UXtA6d5SuRIu+T4Sbj8yMKgbmwIDAQAB";

    @Bean
    public Object aliPayConfigInjector(){
        appid = _appid;
        privateKey = _privateKey;
        notifyURL = _notifyURL;

        return new Object();
    }

    public static String getAppid() {
        return appid;
    }

    public static String getPrivateKey() {
        return privateKey;
    }

    public static String getNotifyURL() {
        return notifyURL;
    }

    public static String getGateway() {
        return gateway;
    }

    public static String getAliPublicKey() {
        return aliPublicKey;
    }
}
