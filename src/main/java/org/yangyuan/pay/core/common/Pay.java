package org.yangyuan.pay.core.common;

import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.bean.common.Trade;
import org.yangyuan.pay.bean.common.TradeToken;
import org.yangyuan.pay.exception.RefundException;

/**
 * 支付定义
 * @author yangyuan
 * @date 2019/1/4 15:03
 * @param <T>
 */
public interface Pay<T extends Trade> {
    
    /**
     * 支付
     * @param trade 订单
     * @return 订单凭证
     */
    TradeToken<?> pay(T trade);

    /**
     * 退款
     * @param trade 订单
     * @throws RefundException 退款失败抛出
     */
    void refund(T trade) throws RefundException;
    
    /**
     * 订单状态查询
     * @param trade 订单
     * @return 订单状态
     */
    TradeStatus status(T trade);
    
}
