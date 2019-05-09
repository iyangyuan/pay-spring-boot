package org.yangyuan.pay.http.client;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.yangyuan.pay.http.client.common.AbstractHttpClient;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * 
 * 普通通用HTTP客户端
 * <p><b>普通</b>的含义即：非HTTP SSL客户端</p>
 * @author yangyuan
 * @date 2018年4月17日
 */
public class HttpClient extends AbstractHttpClient {
    /**
     * 核心请求对象
     */
    private CloseableHttpClient httpclient;
    
    private static final HttpClient CLIENT = new HttpClient();
    
    private HttpClient(){
        /**
         * 请求配置
         */
        RequestConfig globalConfig = RequestConfig.
                                        custom().
                                        setCookieSpec(CookieSpecs.DEFAULT).
                                        setSocketTimeout(10000).
                                        setConnectTimeout(20000).
                                        setConnectionRequestTimeout(20000).
                                        build();
        /**
         * cookie容器
         */
        CookieStore cookieStore = new BasicCookieStore();
        
        /**
         * 核心请求对象
         */
        httpclient = HttpClients.
                        custom().
                        setDefaultRequestConfig(globalConfig).
                        setDefaultCookieStore(cookieStore).
                        setRetryHandler(new HttpRequestRetryHandler() {
                            @Override
                            public boolean retryRequest(IOException exception, int retryTimes, HttpContext httpContext) {
                                if(retryTimes > 10){  //最多重试10次
                                    return false;
                                }
                                if(Arrays.asList(InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class).contains(exception.getClass())){  //此类异常不进行重试
                                    return false;
                                }
                                return true;  //重点是这，非幂等的post请求也进行重试
                            }
                        }).
                        build();
    }
    
    @Override
    protected CloseableHttpClient getCloseableHttpClient() {
        return httpclient;
    }
    
    public static HttpClient getClient(){
        return CLIENT;
    }
    
}
