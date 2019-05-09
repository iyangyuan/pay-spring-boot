package org.yangyuan.pay.core;

import org.yangyuan.pay.core.common.AbstractNoticeManager;

/**
 * 通知(回调)统一调度器辅助工具
 * @author yangyuan
 * @date 2017年12月9日
 */
public class NoticeManagers {
    /**
     * 默认通知(回调)统一调度器实现
     */
    private static final AbstractNoticeManager DEFAULT_NOTICE_MANAGER = new AbstractNoticeManager() {};
    
    /**
     * 获取默认通知(回调)统一调度器实现
     * @return
     */
    public static AbstractNoticeManager getDefaultManager(){
        return DEFAULT_NOTICE_MANAGER;
    }
    
}
