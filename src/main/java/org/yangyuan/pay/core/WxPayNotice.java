package org.yangyuan.pay.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.bean.WxPayNoticeInfo;
import org.yangyuan.pay.config.CommonConfig;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.core.common.PayNotice;
import org.yangyuan.pay.util.PayMD5;
import org.yangyuan.pay.util.PayMapUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 微信回调实现
 * @author yangyuan
 * @date 2017年5月8日
 */
public class WxPayNotice implements PayNotice<WxPayNoticeInfo> {
    
    private static Log log = LogFactory.getLog(WxPayNotice.class);

    @Override
    public Map<String, String> receiveParams(HttpServletRequest request) {
        ServletInputStream sis = null;
        Map<String, String> params = new HashMap<>();
        try {
            //从流中解析出字符串
            byte[] buffer = new byte[8*1024];
            int len = 0;
            sis = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len = sis.read(buffer))>0){
                baos.write(buffer, 0, len);
            }
            String data = new String(baos.toByteArray(), CommonConfig.UNIFY_CHARSET);

            //将xml字符串转换成Document对象
            Document paramsDocument = DocumentHelper.parseText(data);
            Element paramsRootElement = paramsDocument.getRootElement();

            //将xml参数转换成map集合
            for(Iterator<Element> it = paramsRootElement.elementIterator();it.hasNext();){
                Element element = it.next();
                params.put(element.getName(), element.getText());
            }

            return params;
        } catch (Exception e) {
            throw new  RuntimeException(e);
        }
    }

    @Override
    public TradeStatus execute(Map<String, String> params, WxPayNoticeInfo info) {
        log.info("[微信-支付回调]接收回调\n" + JSON.toJSONString(params));
        
        /**
         * 验证
         */
        List<Object> filter = new ArrayList<Object>();
        filter.add("sign");
        Map<String,String> _params = PayMapUtil.filter(params.getClass(), params, filter);
        String sign = params.get("sign");
        String _sign = signMD5(_params);
        if(!_sign.equals(sign)){
            log.warn("[微信-支付回调]签名验证失败，请检查接口是否有恶意请求");
            return null;
        }
        
        /**
         * 封装
         */
        info.setAppid(PayMapUtil.getStringText(params, "appid"));
        info.setMchId(PayMapUtil.getStringText(params, "mch_id"));
        info.setDeviceInfo(PayMapUtil.getStringText(params, "device_info"));
        info.setNonceStr(PayMapUtil.getStringText(params, "nonce_str"));
        info.setResultCode(PayMapUtil.getStringText(params, "result_code"));
        info.setErrCode(PayMapUtil.getStringText(params, "err_code"));
        info.setErrCodeDes(PayMapUtil.getStringText(params, "err_code_des"));
        info.setOpenid(PayMapUtil.getStringText(params, "openid"));
        info.setIsSubscribe(PayMapUtil.getStringText(params, "is_subscribe"));
        info.setTradeType(PayMapUtil.getStringText(params, "trade_type"));
        info.setBankType(PayMapUtil.getStringText(params, "bank_type"));
        info.setTotalFee(PayMapUtil.getIntegerText(params, "total_fee"));
        info.setFeeType(PayMapUtil.getStringText(params, "fee_type"));
        info.setCashFee(PayMapUtil.getIntegerText(params, "cash_fee"));
        info.setCashFeeType(PayMapUtil.getStringText(params, "cash_fee_type"));
        info.setCouponFee(PayMapUtil.getIntegerText(params, "coupon_fee"));
        info.setCouponCount(PayMapUtil.getIntegerText(params, "coupon_count"));
        info.setTransactionId(PayMapUtil.getStringText(params, "transaction_id"));
        info.setOutTradeNo(PayMapUtil.getStringText(params, "out_trade_no"));
        info.setAttach(PayMapUtil.getStringText(params, "attach"));
        info.setTimeEnd(PayMapUtil.getStringText(params, "time_end"));
        info.setCreateTime(new Date());

        /**
         * 交易状态
         */
        if("SUCCESS".equalsIgnoreCase(params.get("return_code")) 
                && "SUCCESS".equalsIgnoreCase(info.getResultCode())){  //支付成功
            log.info("[微信-支付回调]支付成功");
            return TradeStatus.paySuccess(info.getOutTradeNo());
        }
        log.info("[微信-支付回调]支付失败");  //支付失败
        return TradeStatus.payFail(info.getOutTradeNo());
    }

    @Override
    public void sendResponse(HttpServletResponse response) {
        try {
            Document responseDocument = DocumentHelper.createDocument();
            Element responseRootElement = responseDocument.addElement("xml");
            responseRootElement.addElement("return_code").addCDATA("SUCCESS");
            responseRootElement.addElement("return_msg").addCDATA("OK");
            response.setHeader("content-type", "text/xml;charset=UTF-8");
            response.getWriter().write(responseDocument.asXML());
        } catch (IOException e) {}
    }

    /**
     * MD5签名
     * @param params 待签名参数集合
     * @return 签名
     */
    private String signMD5(Map<String,String> params){
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
}
