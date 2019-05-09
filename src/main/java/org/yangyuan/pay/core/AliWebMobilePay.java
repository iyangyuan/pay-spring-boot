package org.yangyuan.pay.core;

import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.bean.AliTrade;
import org.yangyuan.pay.bean.common.TradeToken;
import org.yangyuan.pay.core.common.AbstractAliPay;
import org.yangyuan.pay.core.common.AliPayBizOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝手机网站支付
 * @Auther: yangyuan
 * @Date: 2019/1/8 17:59
 */
public class AliWebMobilePay extends AbstractAliPay {
    @Override
    public TradeToken<String> pay(AliTrade trade) {
        /*
            获取链接
         */
        String l = link(trade, new AliPayBizOptions() {
            @Override
            public Map<String, String> buildBizParams(AliTrade trade) {
                Map<String, String> params = new HashMap<>();

                /*
                    必选项
                 */
                if(StringUtils.isBlank(trade.getOutTradeNo())){
                    throw new RuntimeException("out_trade_no商户订单号不能为空！");
                }
                if(StringUtils.isBlank(trade.getSubject())){
                    throw new RuntimeException("subject商品标题不能为空！");
                }
                if(StringUtils.isBlank(trade.getTotalAmount())){
                    throw new RuntimeException("total_amount订单总金额不能为空！");
                }
                params.put("product_code", trade.getProductCode());
                params.put("out_trade_no", trade.getOutTradeNo());
                params.put("subject", trade.getSubject());
                params.put("total_amount", trade.getTotalAmount());

                /*
                    可选项
                 */
                if(StringUtils.isNotBlank(trade.getBody())){
                    params.put("body", trade.getBody());
                }
                if(StringUtils.isNotBlank(trade.getTimeoutExpress())){
                    params.put("timeout_express", trade.getTimeoutExpress());
                }
                if(StringUtils.isNotBlank(trade.getGoodsType())){
                    params.put("goods_type", trade.getGoodsType());
                }
                if(StringUtils.isNotBlank(trade.getPassbackParams())){
                    params.put("passback_params", trade.getPassbackParams());
                }
                if(StringUtils.isNotBlank(trade.getStoreId())){
                    params.put("store_id", trade.getStoreId());
                }
                if(StringUtils.isNotBlank(trade.getQuitUrl())){
                    params.put("quit_url", trade.getQuitUrl());
                }

                return params;
            }

            @Override
            public String responseBizPropName() {
                return null;
            }
        });

        return new TradeToken<String>() {
            @Override
            public String value() {
                return l;
            }
        };
    }
}
