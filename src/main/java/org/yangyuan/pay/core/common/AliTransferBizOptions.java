package org.yangyuan.pay.core.common;

import org.yangyuan.pay.bean.AliTransferTrade;

import java.util.Map;

/**
 * 支付宝转账业务参数封装
 * @Auther: yangyuan
 * @Date: 2019/1/15 13:52
 */
public interface AliTransferBizOptions extends AliBizOptions{
    /**
     * 构造转账业务参数
     * @param aliTransferTrade
     */
    Map<String ,String> buildBizParams(AliTransferTrade aliTransferTrade);
}
