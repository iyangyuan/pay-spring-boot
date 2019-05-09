package org.yangyuan.pay.core;

import org.yangyuan.pay.core.common.AbstractPayManager;
import org.yangyuan.pay.core.common.PayManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付统一调度器辅助工具
 * @author yangyuan
 * @date 2017年5月10日
 */
public class PayManagers {
    private static final List<PayManager> PAY_MANAGERS = new ArrayList<PayManager>();
    
    /**
     * 注册调度器
     * @param manager 调度器
     */
    public static void register(PayManager manager){
        PAY_MANAGERS.add(manager);
    }
    
    /**
     * 根据订单号匹配调度器
     * @param outTradeNo 商户订单号
     * @return
     */
    public static PayManager find(String outTradeNo){
        for(PayManager payManager : PAY_MANAGERS){
            if(!(payManager instanceof AbstractPayManager)){
                continue;
            }
            if(((AbstractPayManager) payManager).matches(outTradeNo)){
                return payManager;
            }
        }
        
        throw new RuntimeException("none PayManager can be matched by outTradeNo[" + outTradeNo + "]!");
    }
}
