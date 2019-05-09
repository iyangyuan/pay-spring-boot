package org.yangyuan.pay.core.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.yangyuan.pay.bean.TransferStatus;
import org.yangyuan.pay.bean.WxTransferTrade;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.exception.TransferException;
import org.yangyuan.pay.http.client.HttpsWxClient;
import org.yangyuan.pay.http.client.common.HttpOptions;
import org.yangyuan.pay.http.response.SimpleResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 转账定义微信抽象实现
 * @author yangyuan
 * @date 2019/1/4
 */
public abstract class AbstractWxTransfer extends AbstractWx implements Transfer<WxTransferTrade>{

    private static Log log = LogFactory.getLog(AbstractWxTransfer.class);

    @Override
    public void transfer(WxTransferTrade transferTrade) throws TransferException {
        Map<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(transferTrade.getAppid())){
            params.put("mch_appid", transferTrade.getAppid());
        }
        if(StringUtils.isNotBlank(transferTrade.getMchid())){
            params.put("mchid", transferTrade.getMchid());
        }
        if(StringUtils.isNotBlank(transferTrade.getDeviceInfo())){
            params.put("device_info", transferTrade.getDeviceInfo());
        }
        if(StringUtils.isNotBlank(transferTrade.getNonceStr())){
            params.put("nonce_str", transferTrade.getNonceStr());
        }
        if(StringUtils.isNotBlank(transferTrade.getPartnerTradeNo())){
            params.put("partner_trade_no", transferTrade.getPartnerTradeNo());
        }
        if(StringUtils.isNotBlank(transferTrade.getOpenid())){
            params.put("openid", transferTrade.getOpenid());
        }
        if(StringUtils.isNotBlank(transferTrade.getCheckName())){
            params.put("check_name", transferTrade.getCheckName());
        }
        if(StringUtils.isNotBlank(transferTrade.getReUserName())){
            params.put("re_user_name", transferTrade.getReUserName());
        }
        if(StringUtils.isNotBlank(transferTrade.getAmount())){
            params.put("amount", transferTrade.getAmount());
        }
        if(StringUtils.isNotBlank(transferTrade.getDesc())){
            params.put("desc", transferTrade.getDesc());
        }
        if(StringUtils.isNotBlank(transferTrade.getSpbillCreateIp())){
            params.put("spbill_create_ip", transferTrade.getSpbillCreateIp());
        }
        params.put("sign", signMD5(params));

        /**
         * 请求
         */
        Document document = buildDocFromMap(params);
        SimpleResponse response =
                HttpsWxClient.getClient().post(WxPayConfig.getTransfersURL(), document.asXML(), new HttpOptions() {
                    @Override
                    public String getContentType() {
                        return "text/xml; charset=utf-8";
                    }
                });

        /**
         * 响应通信解析
         */
        if(response.getCode() != 200){
            log.warn("[微信转账]通信失败。响应：\n" + response.getStringBody());
            throw new TransferException("[微信转账]通信失败。响应：\n" + response.getStringBody());
        }
        if(StringUtils.isBlank(response.getStringBody())){
            log.warn("[微信转账]通信失败。没有接收到任何响应数据");
            throw new TransferException("[微信转账]通信失败。没有接收到任何响应数据");
        }

        /**
         * 响应业务解析
         */
        String strResponse = response.getStringBody();
        try {
            Document docResponse = DocumentHelper.parseText(strResponse);
            Element xmlElement = docResponse.getRootElement();

            /**
             * 转账失败
             */
            Element returnCodeElement = xmlElement.element("return_code");
            if(!"SUCCESS".equalsIgnoreCase(returnCodeElement.getTextTrim())){
                Element returnMsgElement = xmlElement.element("return_msg");
                log.warn("[微信转账]请求失败。原因：\n" + returnMsgElement.getTextTrim());
                throw new TransferException("[微信转账]请求失败。原因：\n" + returnMsgElement.getTextTrim());
            }
            Element resultCodeElement = xmlElement.element("result_code");
            if(!"SUCCESS".equalsIgnoreCase(resultCodeElement.getTextTrim())){
                Element errCodeElement = xmlElement.element("err_code");
                Element errCodeDesElement = xmlElement.element("err_code_des");
                log.warn("[微信转账]业务失败。错误代码：\n" + errCodeElement.getTextTrim() + "\n错误描述：\n" + errCodeDesElement.getTextTrim());

                if("SYSTEMERROR".equalsIgnoreCase(errCodeElement.getTextTrim())){ // SYSTEMERROR 请使用原单号以及原请求参数重试，否则可能造成重复支付等资金风险
                    transfer(transferTrade);
                    return;
                }

                throw new TransferException("[微信转账]请求失败。多次重试仍然失败！");
            }

            /**
             * 转账成功
             */
            String tradeNo = xmlElement.element("partner_trade_no").getTextTrim();
            String outTradeNo = xmlElement.element("payment_no").getTextTrim();
        } catch (Exception e) {
            log.error("[微信宝转账]出现意料之外的严重异常，请及时处理！有可能需要手动对账！\n" + strResponse, e);
            throw new TransferException("[微信宝转账]出现意料之外的严重异常，请及时处理！有可能需要手动对账！\n" + strResponse, e);
        }
    }

    @Override
    public TransferStatus status(WxTransferTrade transferTrade) {
        Map<String, String> params = new HashMap<String, String>();
        if(StringUtils.isNotBlank(transferTrade.getAppid())){
            params.put("appid", transferTrade.getAppid());
        }
        if(StringUtils.isNotBlank(transferTrade.getMchid())){
            params.put("mch_id", transferTrade.getMchid());
        }
        if(StringUtils.isNotBlank(transferTrade.getNonceStr())){
            params.put("nonce_str", transferTrade.getNonceStr());
        }
        if(StringUtils.isNotBlank(transferTrade.getPartnerTradeNo())){
            params.put("partner_trade_no", transferTrade.getPartnerTradeNo());
        }
        params.put("sign", signMD5(params));

        /**
         * 请求
         */
        Document document = buildDocFromMap(params);
        SimpleResponse response =
                HttpsWxClient.getClient().post(WxPayConfig.getTransfersQueryURL(), document.asXML(), new HttpOptions() {

                    @Override
                    public String getContentType() {
                        return "text/xml; charset=utf-8";
                    }

                });

        /**
         * 响应通信解析
         */
        if(response.getCode() != 200){
            log.warn("[微信转账查询]通信失败。响应：\n" + response.getStringBody());
            return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
        }
        if(StringUtils.isBlank(response.getStringBody())){
            log.warn("[微信转账查询]通信失败。没有接收到任何响应数据");
            return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
        }

        /**
         * 响应业务解析
         */
        String strResponse = response.getStringBody();
        try {
            Document docResponse = DocumentHelper.parseText(strResponse);
            Element xmlElement = docResponse.getRootElement();

            /**
             * 转账失败
             */
            Element returnCodeElement = xmlElement.element("return_code");
            if(!"SUCCESS".equalsIgnoreCase(returnCodeElement.getTextTrim())){
                Element returnMsgElement = xmlElement.element("return_msg");
                log.warn("[微信转账查询]请求失败。原因：\n" + returnMsgElement.getTextTrim());
                return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
            }
            Element resultCodeElement = xmlElement.element("result_code");
            if("FAIL".equalsIgnoreCase(resultCodeElement.getTextTrim())){  //一般情况下说明订单不存在
                log.warn("[微信转账查询]微信返回[FAIL]状态，确认转账失败。\n" + strResponse);
                return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
            }

            /**
             * 转账成功
             */
            Element statusElement = xmlElement.element("status");
            if("SUCCESS".equalsIgnoreCase(statusElement.getTextTrim()) ||
                    "PROCESSING".equalsIgnoreCase(statusElement.getTextTrim())){
                log.warn("[微信转账查询]确认转账成功。\n" + strResponse);
                return TransferStatus.successStatus(transferTrade.getPartnerTradeNo());
            }

            /**
             * 所有未知状态视为转账失败
             */
            log.warn("[微信转账查询]检测到未知状态，默认转账失败。\n" + strResponse);
            return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
        } catch (Exception e) {
            log.error("[微信宝转账]出现意料之外的严重异常，请及时处理！有可能需要手动对账！\n" + strResponse, e);
            return TransferStatus.failStatus(transferTrade.getPartnerTradeNo());
        }
    }
}
