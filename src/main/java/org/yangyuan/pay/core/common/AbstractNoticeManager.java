package org.yangyuan.pay.core.common;

import org.yangyuan.pay.bean.AliPayNoticeInfo;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.bean.WxPayNoticeInfo;
import org.yangyuan.pay.bean.common.NoticeInfo;
import org.yangyuan.pay.core.AliPayNotice;
import org.yangyuan.pay.core.WxPayNotice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 通知(回调)统一调度器定义抽象实现
 * @author yangyuan
 * @date 2017年5月9日
 */
public abstract class AbstractNoticeManager implements NoticeManager{
    /**
     * 回调核心实现
     */
    private static AliPayNotice aliPayNotice = new AliPayNotice();
    private static WxPayNotice wxPayNotice = new WxPayNotice();

    @Override
    public Map<String, String> receiveAliParams(HttpServletRequest request) {
        return aliPayNotice.receiveParams(request);
    }

    @Override
    public Map<String, String> receiveWxParams(HttpServletRequest request) {
        return wxPayNotice.receiveParams(request);
    }

    @Override
    public void sendAliResponse(HttpServletResponse response) {
        aliPayNotice.sendResponse(response);
    }

    @Override
    public void sendWxResponse(HttpServletResponse response) {
        wxPayNotice.sendResponse(response);
    }

    @Override
    public TradeStatus execute(Map<String, String> params, NoticeInfo info) {
        if(info instanceof AliPayNoticeInfo){
            return aliPayNotice.execute(params, (AliPayNoticeInfo) info);
        }
        
        if(info instanceof WxPayNoticeInfo){
            return wxPayNotice.execute(params, (WxPayNoticeInfo) info);
        }

        throw new IllegalArgumentException("不支持的通知类型[" + info.getClass().getSimpleName() + "]");
    }
}
