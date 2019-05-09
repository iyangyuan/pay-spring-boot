package org.yangyuan.pay.http.client.common;

import org.yangyuan.pay.config.CommonConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP客户端通用请求配置
 * @author yangyuan
 * @date 2018年4月17日
 */
public abstract class HttpOptions {
    
    /**
     * 自定义请求头域
     * <p><b>不建议用此方法定义Content-Type域</b></p>
     * <p>定义Content-Type域请用getContentType方法</p>
     * @return 头域
     */
    public Map<String, String> getHeaders(){
        return new HashMap<String, String>();
    }
    
    /**
     * 自定义请求体媒体类型
     * <p>本方法会被getHeaders方法中定义的Content-Type域覆盖</p>
     * @return 媒体类型
     */
    public String getContentType(){
        return "text/plain; charset=utf-8";
    }
    
    /**
     * 自定义请求体编码
     * @return 编码
     */
    public String getRequestBodyCharset(){
        return CommonConfig.UNIFY_CHARSET;
    }
    
    /**
     * 自定义响应体编码
     * @return 编码
     */
    public String getResponseBodyCharset(){
        return CommonConfig.UNIFY_CHARSET;
    }
    
}
