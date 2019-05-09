package org.yangyuan.pay.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yangyuan.pay.bean.AliPayNoticeInfo;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.config.AliPayConfig;
import org.yangyuan.pay.config.CommonConfig;
import org.yangyuan.pay.core.common.PayNotice;
import org.yangyuan.pay.util.PayArrayUtil;
import org.yangyuan.pay.util.PayBase64;
import org.yangyuan.pay.util.PayMapUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 支付宝回调实现
 * @author yangyuan
 * @date 2017年5月8日
 */
public class AliPayNotice implements PayNotice<AliPayNoticeInfo> {
    
    private static Log log = LogFactory.getLog(AliPayNotice.class);

    private final PublicKey ALI_PUBLIC_KEY;

    public AliPayNotice() {
        try{
            /*
                创建支付宝公钥
             */
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(PayBase64.decode(AliPayConfig.getAliPublicKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            ALI_PUBLIC_KEY = keyFactory.generatePublic(x509EncodedKeySpec);
        }catch(Exception e){
            throw new RuntimeException("创建RSA密钥异常", e);
        }
    }

    @Override
    public Map<String, String> receiveParams(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<String> iterator = parameterMap.keySet().iterator();
        while(iterator.hasNext()){
            String name = iterator.next();
            String[] values = parameterMap.get(name);
            params.put(name, PayArrayUtil.join(Arrays.asList(values), ","));
        }

        return params;
    }

    @Override
    public TradeStatus execute(Map<String, String> params, AliPayNoticeInfo info) {
        log.info("[支付宝-支付回调]接收回调\n" + JSON.toJSONString(params));
        
        /*
            验证签名
         */
        if(!verify(params)){
            log.warn("[支付宝-支付回调]签名验证失败，请检查接口是否有恶意请求");
            return null;
        }
        log.info("[支付宝-支付回调]签名验证成功");
        
        /*
            信息封装
         */
        info.setNotifyTime(PayMapUtil.getDateText(params, "notify_time"));
        info.setNotifyType(PayMapUtil.getStringText(params, "notify_type"));
        info.setNotifyId(PayMapUtil.getStringText(params, "notify_id"));
        info.setAppId(PayMapUtil.getStringText(params, "app_id"));
        info.setCharset(PayMapUtil.getStringText(params, "charset"));
        info.setVersion(PayMapUtil.getStringText(params, "version"));
        info.setSign(PayMapUtil.getStringText(params, "sign"));
        info.setSignType(PayMapUtil.getStringText(params, "sign_type"));
        info.setOutTradeNo(PayMapUtil.getStringText(params, "out_trade_no"));
        info.setOutBizNo(PayMapUtil.getStringText(params, "out_biz_no"));
        info.setSubject(PayMapUtil.getStringText(params, "subject"));
        info.setTradeNo(PayMapUtil.getStringText(params, "trade_no"));
        info.setTradeStatus(PayMapUtil.getStringText(params, "trade_status"));
        info.setSellerId(PayMapUtil.getStringText(params, "seller_id"));
        info.setSellerEmail(PayMapUtil.getStringText(params, "seller_email"));
        info.setBuyerId(PayMapUtil.getStringText(params, "buyer_id"));
        info.setBuyerLogonId(PayMapUtil.getStringText(params, "buyer_logon_id"));
        info.setTotalAmount(PayMapUtil.getDoubleText(params, "total_amount"));
        info.setReceiptAmount(PayMapUtil.getDoubleText(params, "receipt_amount"));
        info.setInvoiceAmount(PayMapUtil.getDoubleText(params, "invoice_amount"));
        info.setBuyerPayAmount(PayMapUtil.getDoubleText(params, "buyer_pay_amount"));
        info.setPointAmount(PayMapUtil.getDoubleText(params, "point_amount"));
        info.setRefundFee(PayMapUtil.getDoubleText(params, "refund_fee"));
        info.setBody(PayMapUtil.getStringText(params, "body"));
        info.setGmtCreate(PayMapUtil.getDateText(params, "gmt_create"));
        info.setGmtPayment(PayMapUtil.getDateText(params, "gmt_payment"));
        info.setGmtClose(PayMapUtil.getDateText(params, "gmt_close"));
        info.setFundBillList(PayMapUtil.getStringText(params, "fund_bill_list"));
        info.setPassbackParams(PayMapUtil.getStringText(params, "passback_params"));
        info.setVoucherDetailList(PayMapUtil.getStringText(params, "voucher_detail_list"));
        info.setGmtRefund(PayMapUtil.getDateText(params, "gmt_refund"));
        info.setCreateTime(new Date());

        /*
            交易状态
         */
        String tradeStatus = info.getTradeStatus();
        if(tradeStatus.equalsIgnoreCase("TRADE_SUCCESS") 
                || tradeStatus.equalsIgnoreCase("TRADE_FINISHED")){  //支付成功
            log.info("[支付宝-支付回调]支付成功[" + tradeStatus + "]");
            return TradeStatus.paySuccess(info.getOutTradeNo());
        }
        if(tradeStatus.equalsIgnoreCase("WAIT_BUYER_PAY")){  //等待付款
            log.info("[支付宝-支付回调]等待付款[" + tradeStatus + "]");
            return TradeStatus.unknown(info.getOutTradeNo());
        }
        log.info("[支付宝-支付回调]支付失败[" + tradeStatus + "]");  //支付失败
        return TradeStatus.payFail(info.getOutTradeNo());
    }

    @Override
    public void sendResponse(HttpServletResponse response) {
        try {
            response.setHeader("content-type", "text/html;charset=UTF-8");
            response.getWriter().write("success");
        } catch (IOException e) {}
    }

    /**
     * 验证签名
     * @param params
     * @return
     */
    private boolean verify(Map<String, String> params){
        String sign = params.remove("sign");
        params.remove("sign_type");

        String content = signContent(params);

        return verifyRSA2Sign(content, sign);
    }

    /**
     * 获取待签名字符串
     * @param params
     * @return
     */
    private String signContent(Map<String, String> params){
        try {
            List<String> pairs = new ArrayList<String>();
            for(Map.Entry<String, String> entry : params.entrySet()){
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

            return builder.toString();
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
    private boolean verifyRSA2Sign(String content, String sign){
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
