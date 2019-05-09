package org.yangyuan.pay.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yangyuan.pay.bean.WxTrade;
import org.yangyuan.pay.bean.common.TradeToken;
import org.yangyuan.pay.config.CommonConfig;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.core.common.AbstractWxPay;
import org.yangyuan.pay.http.client.HttpClient;
import org.yangyuan.pay.http.response.SimpleResponse;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信扫码支付
 * @Auther: yangyuan
 * @Date: 2019/1/8 18:00
 */
public class WxQrcodePay extends AbstractWxPay {

    private static Log log = LogFactory.getLog(WxQrcodePay.class);

    @Override
    public TradeToken<String> pay(WxTrade trade) {
        try{
            String link = prepay(trade);

            return new TradeToken<String>() {
                @Override
                public String value() {
                    return link;
                }
            };
        }catch(Exception e){
            log.error("[微信支付]发起微信支付异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 预支付
     * @param trade
     * @return
     * @throws DocumentException
     */
    private String prepay(WxTrade trade) throws DocumentException {
        Jedis redis = null;
        try{
            redis = CommonConfig.getJedisPool().getResource();
            String codeUrlKey = "pay:wx:out_trade_no:".concat(trade.getOutTradeNo()).concat(":code_url");

            /**
             * 从缓存中寻找二维码链接
             */
            String reply = redis.get(codeUrlKey);
            if(StringUtils.isNotBlank(reply)){
                return reply;
            }

            /**
             * 远程请求二维码链接
             */
            log.info("[微信支付]开始预支付");

            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", trade.getAppid());
            params.put("mch_id", trade.getMchid());
            params.put("nonce_str", trade.getNonceStr());
            params.put("body", trade.getBody());
            params.put("detail", trade.getDetail());
            params.put("out_trade_no", trade.getOutTradeNo());
            params.put("total_fee", trade.getTotalFee());
            params.put("spbill_create_ip", trade.getSpbillCreateIp());
            params.put("notify_url", trade.getNotifyUrl());
            params.put("trade_type", trade.getTradeType());
            params.put("product_id", trade.getProductId());
            params.put("sign", signMD5(params));

            log.info("[微信支付]预支付参数构造完成\n" + JSON.toJSONString(params));

            Document paramsDoc = buildDocFromMap(params);

            log.info("[微信支付]预支付XML参数构造完成\n" + paramsDoc.asXML());

            log.info("[微信支付]开始请求微信服务器进行预支付");

            SimpleResponse response = HttpClient.getClient().post(WxPayConfig.getUnifiedorderURL(), paramsDoc.asXML());
            if(response.getCode() != 200){
                throw new RuntimeException("请求预支付通信失败, HTTP STATUS[" + response.getCode() + "]");
            }
            String responseBody = response.getStringBody();

            log.info("[微信支付]预支付通信成功\n" + responseBody);

            /**
             * 解析响应数据
             */
            Document responseDoc = DocumentHelper.parseText(responseBody);
            Element codeUrlElement = responseDoc.getRootElement().element("code_url");
            if(codeUrlElement == null){
                throw new RuntimeException("请求预支付未找到二维码链接(code_url)");
            }
            String codeUrl = codeUrlElement.getTextTrim();

            log.info("[微信支付]成功获取二维码链接[" + codeUrl + "]");

            /**
             * 缓存二维码链接
             */
            redis.set(codeUrlKey, codeUrl);
            redis.expire(codeUrlKey, 7170);

            return codeUrl;
        }finally{
            if(redis != null){
                redis.close();
                redis = null;
            }
        }
    }
}
