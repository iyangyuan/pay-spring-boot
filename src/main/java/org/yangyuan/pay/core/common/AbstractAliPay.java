package org.yangyuan.pay.core.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.bean.AliTrade;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.exception.RefundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付抽象实现
 * @Auther: yangyuan
 * @Date: 2019/1/4 16:15
 */
public abstract class AbstractAliPay extends AbstractAli implements Pay<AliTrade>{
    @Override
    public void refund(AliTrade trade) throws RefundException {
        try {
            /*
                请求
             */
            JSONObject response = request(trade, new AliPayBizOptions() {
                @Override
                public Map<String, String> buildBizParams(AliTrade trade) {
                    Map<String, String> params = new HashMap<>();
                    /*
                        必选项
                     */
                    if(StringUtils.isBlank(trade.getOutTradeNo())){
                        throw new RefundException("out_trade_no商户订单号不能为空！");
                    }
                    if(StringUtils.isBlank(trade.getRefundAmount())){
                        throw new RefundException("refund_amount退款金额不能为空！");
                    }
                    params.put("out_trade_no", trade.getOutTradeNo());
                    params.put("refund_amount", trade.getRefundAmount());

                    /*
                        可选项
                     */
                    if(StringUtils.isNotBlank(trade.getRefundReason())){
                        params.put("refund_reason", trade.getRefundReason());
                    }
                    if(StringUtils.isNotBlank(trade.getOutRequestNo())){
                        params.put("out_request_no", trade.getOutRequestNo());
                    }

                    return params;
                }

                @Override
                public String responseBizPropName() {
                    return "alipay_trade_refund_response";
                }
            });

            /*
                请求结果
             */
            if(!requestSuccess(response)){
                throw new RefundException("退款失败。\n" + response.toJSONString());
            }

        }catch (Exception e){
            throw new RefundException(e);
        }
    }

    @Override
    public TradeStatus status(AliTrade trade) {
        try {
            /*
                请求
             */
            JSONObject response = request(trade, new AliPayBizOptions() {
                @Override
                public Map<String, String> buildBizParams(AliTrade trade) {
                    Map<String, String> params = new HashMap<>();
                    /*
                        必选项
                     */
                    if(StringUtils.isBlank(trade.getOutTradeNo())){
                        throw new RefundException("out_trade_no商户订单号不能为空！");
                    }
                    params.put("out_trade_no", trade.getOutTradeNo());

                    return params;
                }

                @Override
                public String responseBizPropName() {
                    return "alipay_trade_query_response";
                }
            });

            /*
                业务状态
             */
            if(!requestSuccess(response)){
                if("ACQ.TRADE_NOT_EXIST".equalsIgnoreCase(response.getString("sub_code"))){  //订单不存在，支付失败
                    return TradeStatus.payFail(trade.getOutTradeNo());
                }
                return TradeStatus.unknown(trade.getOutTradeNo());  //其他异常状态认为状态未知
            }

            /*
                解析订单状态
             */
            String tradeStatus = response.getString("trade_status");
            if("WAIT_BUYER_PAY".equalsIgnoreCase(tradeStatus)){  //交易创建，等待买家付款，认为状态未知
                return TradeStatus.unknown(trade.getOutTradeNo());
            }
            if("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)){  //未付款交易超时关闭，或支付完成后全额退款，支付失败
                return TradeStatus.payFail(trade.getOutTradeNo());
            }
            if("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)){  //交易支付成功
                return TradeStatus.paySuccess(trade.getOutTradeNo());
            }
            if("TRADE_FINISHED".equalsIgnoreCase(tradeStatus)){  //交易结束，不可退款，支付成功
                return TradeStatus.paySuccess(trade.getOutTradeNo());
            }

            return TradeStatus.unknown(trade.getOutTradeNo());  //其他订单状态认为状态未知
        }catch (Exception e){
            return TradeStatus.unknown(trade.getOutTradeNo());  //与支付宝通信失败，则认为状态未知
        }
    }

}
