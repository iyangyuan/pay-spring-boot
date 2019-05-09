package org.yangyuan.pay.core.common;

import org.yangyuan.pay.bean.TransferStatus;
import org.yangyuan.pay.bean.common.TransferTrade;
import org.yangyuan.pay.exception.TransferException;

/**
 * 转账定义
 * @author yangyuan
 * @date 2019/1/4
 */
public interface Transfer<T extends TransferTrade> {
    /**
     * 转账
     * @param transferTrade 转账订单
     */
    void transfer(T transferTrade) throws TransferException;
    
    /**
     * 订单转账状态查询
     * @param transferTrade 转账订单，包含商户订单号(非第三方订单号)
     * @return 转账状态
     */
    TransferStatus status(T transferTrade);
}
