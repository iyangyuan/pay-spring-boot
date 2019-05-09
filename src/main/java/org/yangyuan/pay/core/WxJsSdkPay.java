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
 * 微信JS-SDK支付
 * @Auther: yangyuan
 * @Date: 2019/1/8 18:00
 */
public class WxJsSdkPay extends AbstractWxPay {

    private static Log log = LogFactory.getLog(WxJsSdkPay.class);

    @Override
    public TradeToken<Map<String, String>> pay(WxTrade trade) {
        try{
            Map<String, String> params = new HashMap<>();
            String prepayid = prepay(trade);
            params.put("appId", trade.getAppid());
            params.put("timeStamp", String.valueOf(System.currentTimeMillis()/1000));
            params.put("nonceStr", trade.getNonceStr());
            params.put("package", "prepay_id=" + prepayid);
            params.put("signType", "MD5");
            params.put("paySign", signMD5(params));

            return new TradeToken<Map<String, String>>() {
                @Override
                public Map<String, String> value() {
                    return params;
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
            String prepayidKey = "pay:wx:out_trade_no:".concat(trade.getOutTradeNo()).concat(":prepay_id");

            /*
                从缓存中寻找预支付id
             */
            String reply = redis.get(prepayidKey);
            if(StringUtils.isNotBlank(reply)){
                return reply;
            }

            /*
                远程请求预支付id
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
            params.put("openid", trade.getOpenid());
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

            /*
                解析响应数据
             */
            Document responseDoc = DocumentHelper.parseText(responseBody);
            Element prepayIdElement = responseDoc.getRootElement().element("prepay_id");
            if(prepayIdElement == null){
                throw new RuntimeException("请求预支付未找到预支付id(prepay_id)");
            }
            String prepayid = prepayIdElement.getTextTrim();

            log.info("[微信支付]成功获取预支付id[" + prepayid + "]");

            /*
                缓存预支付id
             */
            redis.set(prepayidKey, prepayid);
            redis.expire(prepayidKey, 7170);

            return prepayid;
        }finally{
            if(redis != null){
                redis.close();
                redis = null;
            }
        }
    }
}
