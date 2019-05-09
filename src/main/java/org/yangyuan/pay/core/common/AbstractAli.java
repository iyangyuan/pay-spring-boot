package org.yangyuan.pay.core.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.bean.AliTrade;
import org.yangyuan.pay.bean.AliTransferTrade;
import org.yangyuan.pay.config.AliPayConfig;
import org.yangyuan.pay.config.CommonConfig;
import org.yangyuan.pay.http.client.HttpClient;
import org.yangyuan.pay.http.response.SimpleResponse;
import org.yangyuan.pay.util.PayBase64;
import org.yangyuan.pay.util.PayDateUtil;
import org.yangyuan.pay.util.PayHttpUtil;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支付宝公共方法封装
 * @Auther: yangyuan
 * @Date: 2019/1/11 15:02
 */
public abstract class AbstractAli {

    private final PrivateKey PRIVATE_KEY;
    private final PublicKey ALI_PUBLIC_KEY;

    private static final Pattern JSON_STRING_PATTERN = Pattern.compile("\"([^\"]|(?<=\\\\)\")+\"");

    public AbstractAli() {
        try{
            /*
                创建商户私钥
             */
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(PayBase64.decode(AliPayConfig.getPrivateKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PRIVATE_KEY = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            /*
                创建支付宝公钥
             */
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(PayBase64.decode(AliPayConfig.getAliPublicKey()));
            ALI_PUBLIC_KEY = keyFactory.generatePublic(x509EncodedKeySpec);
        }catch(Exception e){
            throw new RuntimeException("创建RSA密钥异常", e);
        }
    }

    /**
     * 根据订单信息构造支付参数
     * @param aliTrade
     * @return
     */
    protected Map<String, String> buildCommonParams(AliTrade aliTrade){
        Map<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(aliTrade.getAppId())){
            params.put("app_id", aliTrade.getAppId());
        }
        if(StringUtils.isNotBlank(aliTrade.getMethod())){
            params.put("method", aliTrade.getMethod());
        }
        if(StringUtils.isNotBlank(aliTrade.getFormat())){
            params.put("format", aliTrade.getFormat());
        }
        if(StringUtils.isNotBlank(aliTrade.getCharset())){
            params.put("charset", aliTrade.getCharset());
        }
        if(StringUtils.isNotBlank(aliTrade.getVersion())){
            params.put("version", aliTrade.getVersion());
        }
        if(StringUtils.isNotBlank(aliTrade.getNotifyUrl())){
            params.put("notify_url", aliTrade.getNotifyUrl());
        }
        if(StringUtils.isNotBlank(aliTrade.getReturnUrl())){
            params.put("return_url", aliTrade.getReturnUrl());
        }

        return params;
    }

    /**
     * 根据订单信息构造转账参数
     * @param transferTrade
     * @return
     */
    protected Map<String, String> buildCommonParams(AliTransferTrade transferTrade){
        Map<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(transferTrade.getAppid())){
            params.put("app_id", transferTrade.getAppid());
        }
        if(StringUtils.isNotBlank(transferTrade.getMethod())){
            params.put("method", transferTrade.getMethod());
        }
        if(StringUtils.isNotBlank(transferTrade.getFormat())){
            params.put("format", transferTrade.getFormat());
        }
        if(StringUtils.isNotBlank(transferTrade.getCharset())){
            params.put("charset", transferTrade.getCharset());
        }
        if(StringUtils.isNotBlank(transferTrade.getVersion())){
            params.put("version", transferTrade.getVersion());
        }

        return params;
    }

    /**
     * 发起支付请求
     * @param aliTrade
     */
    protected JSONObject request(AliTrade aliTrade, AliPayBizOptions aliPayBizOptions){
         /*
            公共参数
         */
        Map<String,String> params = buildCommonParams(aliTrade);

        /*
            业务参数
         */
        Map<String, String> paramsBiz = aliPayBizOptions.buildBizParams(aliTrade);
        params.put("biz_content", JSON.toJSONString(paramsBiz));

        return request(params, aliPayBizOptions);
    }

    /**
     * 发起转账请求
     * @param transferTrade
     */
    protected JSONObject request(AliTransferTrade transferTrade, AliTransferBizOptions aliTransferBizOptions){
         /*
            公共参数
         */
        Map<String,String> params = buildCommonParams(transferTrade);

        /*
            业务参数
         */
        Map<String, String> paramsBiz = aliTransferBizOptions.buildBizParams(transferTrade);
        params.put("biz_content", JSON.toJSONString(paramsBiz));

        return request(params, aliTransferBizOptions);
    }

    /**
     * 获取支付凭证
     * @param aliTrade
     */
    protected String token(AliTrade aliTrade, AliPayBizOptions aliPayBizOptions){
         /*
            公共参数
         */
        Map<String,String> params = buildCommonParams(aliTrade);

        /*
            业务参数
         */
        Map<String, String> paramsBiz = aliPayBizOptions.buildBizParams(aliTrade);
        params.put("biz_content", JSON.toJSONString(paramsBiz));

        /*
            签名
         */
        signParams(params);

        return PayHttpUtil.appendParamsToURL(params, null);
    }

    /**
     * 获取支付链接
     * @param aliTrade
     */
    protected String link(AliTrade aliTrade, AliPayBizOptions aliPayBizOptions){
         /*
            公共参数
         */
        Map<String,String> params = buildCommonParams(aliTrade);

        /*
            业务参数
         */
        Map<String, String> paramsBiz = aliPayBizOptions.buildBizParams(aliTrade);
        params.put("biz_content", JSON.toJSONString(paramsBiz));

        /*
            签名
         */
        signParams(params);

        return PayHttpUtil.appendParamsToURL(params, AliPayConfig.getGateway());
    }

    /**
     * 参数集合签名
     * @param params
     */
    private void signParams(Map<String ,String> params){
        /*
            补充公共参数
         */
        params.put("timestamp", PayDateUtil.format(new Date()));
        params.put("sign_type", "RSA2");

        /*
            签名
         */
        params.put("sign", signRSA2(params));
    }

    /**
     * 统一请求
     * @param params
     * @param aliBizOptions
     * @return 业务数据
     */
    private JSONObject request(Map<String ,String> params, AliBizOptions aliBizOptions){
        /*
            签名
         */
        signParams(params);

        /*
            请求支付宝网关
         */
        SimpleResponse response = HttpClient.getClient().get(PayHttpUtil.appendParamsToURL(params, AliPayConfig.getGateway()));

        /*
            通信状态
         */
        if(response.getCode() != 200){
            throw new RuntimeException("[支付宝统一请求]通信失败。响应：\n" + response.getStringBody());
        }
        String body = response.getStringBody();
        if(StringUtils.isBlank(body)){
            throw new RuntimeException("[支付宝统一请求]通信失败。没有接收到任何响应数据。");
        }

        /*
            验证签名
         */
        JSONObject json;
        try {
            json = JSON.parseObject(body);
        } catch (Exception e) {
            throw new RuntimeException("[支付宝统一请求]解析响应数据(JSON)失败。");
        }
        String sign =  json.getString("sign");
        String content = extractSignData(body, aliBizOptions.responseBizPropName());
        if(!verifyRSA2Sign(content,sign)){
            throw new RuntimeException("[支付宝统一请求]支付宝签名验证失败。");
        }

        return json.getJSONObject(aliBizOptions.responseBizPropName());
    }

    /**
     * 请求是否成功
     * @param response
     * @return
     */
    protected boolean requestSuccess(JSONObject response){
        return "10000".equals(response.getString("code"));
    }

    /**
     * 以字符串处理的形式提取待签名数据
     * @param data 支付宝响应完整数据
     * @param key 属性名称
     * @return 待签名数据
     */
    protected String extractSignData(String data, String key){
        StringBuilder builder = new StringBuilder(data);

        Matcher matcher = JSON_STRING_PATTERN.matcher(data);
        int start;
        int end;
        while(matcher.find()){
            start = matcher.start();
            end = matcher.end();

            if(key.equals(matcher.group(0))){
                continue;
            }

            builder.replace(start, end, StringUtils.repeat("?", end - start));
        }

        String simpleData = builder.toString();
        int fromIndex = simpleData.indexOf(key) + key.length();
        start = simpleData.indexOf("{", fromIndex);
        end = simpleData.indexOf("}", fromIndex) + 1;

        return data.substring(start, end);
    }

    /**
     * RSA2签名
     * @param content 待签名字符串
     * @return 签名
     */
    protected String signRSA2(String content) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(PRIVATE_KEY);
            signature.update(content.getBytes(CommonConfig.UNIFY_CHARSET));
            byte[] bytes = signature.sign();

            return PayBase64.encode(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA2签名
     * @param params 待签名参数集合
     * @return 签名
     */
    protected String signRSA2(Map<String,String> params){
        try {
            List<String> pairs = new ArrayList<String>();
            for(Map.Entry<String, String> entry : params.entrySet()){
                if(StringUtils.isBlank(entry.getValue())){
                    continue;
                }
                pairs.add(entry.getKey() + "=" + entry.getValue());
            }
            Collections.sort(pairs, String.CASE_INSENSITIVE_ORDER);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < pairs.size(); i++) {
                builder.append(pairs.get(i));
                if(i + 1 < pairs.size()){
                    builder.append("&");
                }
            }

            return signRSA2(builder.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA2签名验证
     * @param content 原始内容
     * @param sign 签名
     * @return
     */
    protected boolean verifyRSA2Sign(String content, String sign){
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(ALI_PUBLIC_KEY);
            signature.update(content.getBytes(CommonConfig.UNIFY_CHARSET));

            return signature.verify(PayBase64.decode(sign));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
