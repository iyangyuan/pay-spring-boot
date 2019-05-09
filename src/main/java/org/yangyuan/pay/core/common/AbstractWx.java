package org.yangyuan.pay.core.common;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.util.PayMD5;

import java.util.*;

/**
 * 微信支付公共方法封装
 * @Auther: yangyuan
 * @Date: 2019/1/18 14:03
 */
public abstract class AbstractWx {

    /**
     * 微信内部错误集合
     */
    protected static final Set<String> WX_SYSTEM_ERROR_SET;

    static {
        /*
            微信内部错误集合初始化
         */
        WX_SYSTEM_ERROR_SET = new HashSet<>();
        WX_SYSTEM_ERROR_SET.add("SYSTEMERROR");
    }

    /**
     * MD5签名
     * @param params 待签名参数集合
     * @return 签名
     */
    protected String signMD5(Map<String,String> params){
        List<String> pairs = new ArrayList<String>();
        for(Map.Entry<String, String> entry : params.entrySet()){
            if(StringUtils.isBlank(entry.getValue())){
                continue;
            }
            pairs.add(entry.getKey() + "=" + entry.getValue() + "&");
        }
        Collections.sort(pairs, String.CASE_INSENSITIVE_ORDER);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < pairs.size(); i++) {
            builder.append(pairs.get(i));
        }

        builder.append("key=");
        builder.append(WxPayConfig.getKey());
        return PayMD5.encode(builder.toString()).toUpperCase();
    }

    /**
     * 将map转换成符合微信xml通信协议的document
     * @param map
     * @return
     */
    protected Document buildDocFromMap(Map<String, String> map){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("xml");
        for(String key : map.keySet()){
            root.addElement(key).addCDATA(map.get(key));
        }

        return document;
    }





}
