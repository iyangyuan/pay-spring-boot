package org.yangyuan.pay.http.client.common;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * 抽象HTTP SSL客户端骨架
 * <p>所有HTTP SSL客户端实现都是专用的，不能通用</p>
 * @author yangyuan
 * @date 2018年4月17日
 */
public abstract class AbstractSSLHttpClient extends AbstractHttpClient{
    /**
     * 核心请求对象
     */
    private CloseableHttpClient httpclient;
    
    public AbstractSSLHttpClient(){
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
                        setSSLSocketFactory(getSSLSocketFactory()).
                        build();
    }
    
    @Override
    protected CloseableHttpClient getCloseableHttpClient() {
        return httpclient;
    }

    /**
     * 获取SSL套接字工厂
     * <p>注意：由于此方法在父类构造方法中调用，子类实现该方法时，不能访问子类的成员变量，否则会造成子类数据访问失败！</p>
     * @return SSL套接字
     */
    protected abstract SSLConnectionSocketFactory getSSLSocketFactory();
}
