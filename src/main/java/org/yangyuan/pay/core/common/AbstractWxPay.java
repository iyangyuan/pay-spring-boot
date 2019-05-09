package org.yangyuan.pay.core.common;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.bean.WxTrade;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.exception.RefundException;
import org.yangyuan.pay.http.client.HttpClient;
import org.yangyuan.pay.http.client.HttpsWxClient;
import org.yangyuan.pay.http.response.SimpleResponse;
import org.yangyuan.pay.util.PayDateUtil;
import org.yangyuan.pay.util.PayStrUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付抽象实现
 * @Auther: yangyuan
 * @Date: 2019/1/4 16:21
 */
public abstract class AbstractWxPay extends AbstractWx implements Pay<WxTrade>{

    private static Log log = LogFactory.getLog(AbstractWxPay.class);

    @Override
    public void refund(WxTrade trade) throws RefundException {
        try {
            /*
                请求参数
             */
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", trade.getAppid());
            params.put("mch_id", trade.getMchid());
            params.put("out_trade_no", trade.getOutTradeNo());
            params.put("out_refund_no", PayDateUtil.format(new Date(), "yyyyMMdd") + System.nanoTime());
            params.put("refund_fee", trade.getTotalFee());
            params.put("total_fee", trade.getTotalFee());
            params.put("op_user_id", trade.getMchid());
            params.put("nonce_str", PayStrUtil.getRandomStringByLength(32));
            params.put("sign", signMD5(params));
            Document docParams = buildDocFromMap(params);
            log.info("[微信支付]申请退款请求参数\n" + docParams.asXML());

            /*
                请求
             */
            SimpleResponse response = HttpsWxClient.getClient().post(WxPayConfig.getOrderRefundURL(), docParams.asXML());
            if(response.getCode() != 200){
                throw new RefundException("无法连接到微信服务器");
            }
            String responseBody = response.getStringBody();
            log.info("[微信支付]申请退款响应参数\n" + responseBody);

            /*
                解析响应数据
             */
            Document docResponse = DocumentHelper.parseText(responseBody);
            Element xmlElement = docResponse.getRootElement();
            Element returnCodeElement = xmlElement.element("return_code");
            if(!"SUCCESS".equalsIgnoreCase(returnCodeElement.getTextTrim())){  //通信状态
                Element returnMsgElement = xmlElement.element("return_msg");
                throw new RefundException("[微信支付]申请退款失败：" + returnMsgElement.getTextTrim());
            }
            Element resultCodeElement = xmlElement.element("result_code");
            if(!"SUCCESS".equalsIgnoreCase(resultCodeElement.getTextTrim())){  //业务状态
                Element errCodeElement = xmlElement.element("err_code");
                throw new RefundException("[微信支付]申请退款失败：" + errCodeElement.getTextTrim());
            }
        } catch (Exception e) {
            throw new RefundException(e);
        }
    }

    @Override
    public TradeStatus status(WxTrade trade) {
        try{
            /**
             * 构造参数
             */
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", trade.getAppid());
            params.put("mch_id", trade.getMchid());
            params.put("out_trade_no", trade.getOutTradeNo());
            params.put("nonce_str", PayStrUtil.getRandomStringByLength(32));
            params.put("sign", signMD5(params));
            Document docParams = buildDocFromMap(params);

            log.info("[微信支付]订单查询请求参数\n" + docParams.asXML());

            /**
             * 请求
             */
            SimpleResponse response = HttpClient.getClient().post(WxPayConfig.getOrderQueryURL(), docParams.asXML());

            /**
             * 通信状态
             */
            if(response.getCode() != 200){
                log.warn("[微信支付]无法连接到微信服务器HTTP STATUS[" + response.getCode() + "]");
                return TradeStatus.unknown(trade.getOutTradeNo());
            }

            /**
             * 解析响应数据
             */
            String responseBody = response.getStringBody();
            log.info("[微信支付]订单查询响应参数\n" + responseBody);
            Document docResponse = DocumentHelper.parseText(responseBody);
            Element xmlElement = docResponse.getRootElement();

            /**
             * 异常流程
             */
            Element errorElement = xmlElement.element("err_code");
            if(errorElement != null){
                if(WX_SYSTEM_ERROR_SET.contains(errorElement.getTextTrim())){
                    log.warn("[微信支付]进入异常流程，发现微信内部异常，业务结束[" + trade.getOutTradeNo() + "]");
                    return TradeStatus.unknown(trade.getOutTradeNo());
                }
                if("ORDERNOTEXIST".equalsIgnoreCase(errorElement.getTextTrim())){
                    log.warn("[微信支付]进入异常流程，订单不存在，标记订单为支付超时，业务结束[" + trade.getOutTradeNo() + "]");
                    return TradeStatus.payFail(trade.getOutTradeNo());
                }

                log.warn("[微信支付]进入异常流程，但出乎系统的意料，无法处理，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.unknown(trade.getOutTradeNo());
            }

            /**
             * 正常流程
             */
            Element stateElement = xmlElement.element("trade_state");
            if(stateElement == null){
                log.warn("[微信支付]进入正常流程，但找不到trade_state节点，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.unknown(trade.getOutTradeNo());
            }
            if("CLOSED".equalsIgnoreCase(stateElement.getTextTrim())){  //订单被关闭
                log.warn("[微信支付]进入正常流程，订单已关闭，标记订单为支付超时，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.payFail(trade.getOutTradeNo());
            }
            if("NOTPAY".equalsIgnoreCase(stateElement.getTextTrim())){  //未支付
                log.warn("[微信支付]进入正常流程，订单未支付，标记订单为支付超时，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.payFail(trade.getOutTradeNo());
            }
            if("REFUND".equalsIgnoreCase(stateElement.getTextTrim())){  //转入退款
                return statusRefund(trade);
            }
            if("REVOKED".equalsIgnoreCase(stateElement.getTextTrim())){  //已撤销（刷卡支付）
                log.warn("[微信支付]进入正常流程，订单已撤销（刷卡支付），标记订单为支付超时，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.payFail(trade.getOutTradeNo());
            }
            if("PAYERROR".equalsIgnoreCase(stateElement.getTextTrim())){  //支付失败(其他原因，如银行返回失败)
                log.warn("[微信支付]进入正常流程，订单支付失败(其他原因，如银行返回失败)，标记订单为支付超时，业务结束[" + trade.getOutTradeNo() + "]");
                return TradeStatus.payFail(trade.getOutTradeNo());
            }
            if("SUCCESS".equalsIgnoreCase(stateElement.getTextTrim())){  //支付成功
                log.warn("[微信支付]支付成功[" + trade.getOutTradeNo() + "]");
                return TradeStatus.paySuccess(trade.getOutTradeNo());
            }

            log.warn("[微信支付]发现意料之外的状态："+ stateElement.getTextTrim() +"，业务结束[" + trade.getOutTradeNo() + "]");
            return TradeStatus.unknown(trade.getOutTradeNo());
        }catch(Exception ex){
            log.error("[微信支付]微信订单状态查询异常\n" + JSON.toJSONString(trade), ex);
            return TradeStatus.unknown(trade.getOutTradeNo());
        }
    }

    /**
     * 退款订单状态查询
     * @param trade 订单
     * @return 订单状态
     * @throws DocumentException
     */
    private TradeStatus statusRefund(WxTrade trade) throws DocumentException {
        /**
         * 构造参数
         */
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", trade.getAppid());
        params.put("mch_id", trade.getMchid());
        params.put("out_trade_no", trade.getOutTradeNo());
        params.put("nonce_str", PayStrUtil.getRandomStringByLength(32));
        params.put("sign", signMD5(params));
        Document docParams = buildDocFromMap(params);
        log.info("[微信支付]退款订单查询请求参数\n" + docParams.asXML());

        /**
         * 请求
         */
        SimpleResponse response = HttpClient.getClient().post(WxPayConfig.getOrderRefundQueryURL(), docParams.asXML());
        if(response.getCode() != 200){
            log.warn("[微信支付]无法连接到微信服务器HTTP STATUS[" + response.getCode() + "]");
            return TradeStatus.refundProgress(trade.getOutTradeNo());
        }

        /**
         * 解析响应数据
         */
        String responseBody = response.getStringBody();
        log.info("[微信支付]微信退款订单查询响应参数\n" + responseBody);
        Document docResponse = DocumentHelper.parseText(responseBody);
        Element xmlElement = docResponse.getRootElement();
        Element returnCodeElement = xmlElement.element("return_code");
        if(!"SUCCESS".equalsIgnoreCase(returnCodeElement.getTextTrim())){  //通信状态
            Element returnMsgElement = xmlElement.element("return_msg");
            log.warn("[微信支付]微信退款订单查询请求失败：" + returnMsgElement.getTextTrim());
            return TradeStatus.refundProgress(trade.getOutTradeNo());
        }
        Element resultCodeElement = xmlElement.element("result_code");
        if(!"SUCCESS".equalsIgnoreCase(resultCodeElement.getTextTrim())){  //业务状态
            Element errCodeElement = xmlElement.element("err_code");
            log.warn("[微信支付]微信退款订单查询业务失败：" + errCodeElement.getTextTrim());
            return TradeStatus.refundProgress(trade.getOutTradeNo());
        }
        Element refundStatusElement = xmlElement.element("refund_status_0");
        if(refundStatusElement == null){
            log.warn("[微信支付]微信退款订单查询找不到refund_status_0字段");
            return TradeStatus.refundProgress(trade.getOutTradeNo());
        }
        if("PROCESSING".equalsIgnoreCase(refundStatusElement.getTextTrim())){  //退款中
            log.warn("[微信支付]退款中[" + trade.getOutTradeNo() + "]");
            return TradeStatus.refundProgress(trade.getOutTradeNo());
        }
        if("SUCCESS".equalsIgnoreCase(refundStatusElement.getTextTrim())){  //退款成功
            log.warn("[微信支付]退款成功[" + trade.getOutTradeNo() + "]");
            return TradeStatus.refundSuccess(trade.getOutTradeNo());
        }
        if("FAIL".equalsIgnoreCase(refundStatusElement.getTextTrim())){  //退款失败
            log.warn("[微信支付]退款失败[" + trade.getOutTradeNo() + "]");
            return TradeStatus.refundFail(trade.getOutTradeNo());
        }

        log.warn("[微信支付]微信退款订单查询发现意料之外的状态，请及时处理！状态[" + refundStatusElement.getTextTrim() + "]");
        return TradeStatus.refundProgress(trade.getOutTradeNo());
    }

}
