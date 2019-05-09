package org.yangyuan.pay.core.common;

/**
 * 支付宝业务参数封装
 * @Auther: yangyuan
 * @Date: 2019/1/15 14:13
 */
public interface AliBizOptions {
    /**
     * 支付宝同步响应中的业务数据属性名称
     * <br>
     * 用来验证签名和提取业务数据
     * @return
     */
    String responseBizPropName();
}
