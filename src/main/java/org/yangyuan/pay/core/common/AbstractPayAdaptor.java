package org.yangyuan.pay.core.common;

import org.yangyuan.pay.bean.TradeStatus;

/**
 * 支付适配器抽象实现
 * @author yangyuan
 * @date 2017年5月10日
 */
public abstract class AbstractPayAdaptor implements PayAdaptor{
    /**
     * 订单状态分发
     * @param status 订单状态
     */
    public void doTradeStatus(TradeStatus status){
        
        if(status.isPaySuccess()){
            doPaySuccess(status.getTradeNo());
            return;
        }
        
        if(status.isPayFail()){
            doPayFail(status.getTradeNo());
            return;
        }
        
        if(status.isRefundSuccess()){
            doRefundSuccess(status.getTradeNo());
            return;
        }
        
        if(status.isRefundFail()){
            doRefundFail(status.getTradeNo());
            return;
        }
        
    }
}
