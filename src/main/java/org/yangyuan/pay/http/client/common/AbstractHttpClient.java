package org.yangyuan.pay.http.client.common;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.yangyuan.pay.http.response.SimpleResponse;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 抽象HTTP客户端骨架
 * @author yangyuan
 * @date 2018年4月17日
 */
public abstract class AbstractHttpClient {
    
    /**
     * 获取核心请求实例
     * @return
     */
    protected abstract CloseableHttpClient getCloseableHttpClient();
    
    /**
     * head请求
     * @param url 请求地址
     * @return
     */
    public SimpleResponse head(String url){
        return head(url, null);
    }
    
    /**
     * head请求
     * @param url 请求地址
     * @param options 请求配置
     * @return
     */
    public SimpleResponse head(String url, HttpOptions options){
        HttpHead request = new HttpHead(url);
        
        if(options == null){
            options = new HttpOptions() {};
        }
        
        return sendRequest(request, options);
    }
    
    /**
     * get请求
     * @param url 请求地址
     * @return
     */
    public SimpleResponse get(String url) {
        return get(url, null);
    }
    
    /**
     * get请求
     * @param url 请求地址
     * @param options 请求配置
     * @return
     */
    public SimpleResponse get(String url, HttpOptions options) {
        HttpGet request = new HttpGet(url);
        
        if(options == null){
            options = new HttpOptions() {};
        }
        
        return sendRequest(request, options);
    }
    
    /**
     * post请求
     * @param url 请求地址
     * @param content 请求体
     * @return
     */
    public SimpleResponse post(String url, String content) {
        return post(url, content, null);
    }

    public SimpleResponse post(String url, byte[] bytes) {
        return post(url, bytes, null);
    }

    /**
     * post请求
     * @param url 请求地址
     * @param content 请求体
     * @param options 请求配置
     * @return
     */
    public SimpleResponse post(String url, String content, HttpOptions options) {
        HttpPost request = new HttpPost(url);
        if(options == null){
            options = new HttpOptions() {};
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(content, options.getRequestBodyCharset());
        } catch (Exception e) {
            throw new SecurityException(e);
        }
        entity.setContentType(options.getContentType());
        request.setEntity(entity);
        return sendRequest(request, options);
    }

    public SimpleResponse post(String url, byte[] bytes, HttpOptions options) {
        HttpPost request = new HttpPost(url);
        if(options == null){
            options = new HttpOptions() {};
        }
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(bytes, ContentType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
        request.setEntity(entity);
        return sendRequest(request, options);
    }

    /**
     * put请求
     * @param url 请求地址
     * @param content 请求体
     * @return
     */
    public SimpleResponse put(String url, String content) {
        return put(url, content, null);
    }
    
    /**
     * put请求
     * @param url 请求地址
     * @param content 请求体
     * @param options 请求配置
     * @return
     */
    public SimpleResponse put(String url, String content, HttpOptions options) {
        HttpPut request = new HttpPut(url);
        if(options == null){
            options = new HttpOptions() {};
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(content, options.getRequestBodyCharset());
        } catch (Exception e) {
            throw new SecurityException(e);
        }
        entity.setContentType(options.getContentType());
        request.setEntity(entity);
        return sendRequest(request, options);
    }
    
    /**
     * delete请求
     * @param url 请求地址
     * @return
     */
    public SimpleResponse delete(String url) {
        return delete(url, null);
    }
    
    /**
     * delete请求
     * @param url 请求地址
     * @param options 请求配置
     * @return
     */
    public SimpleResponse delete(String url, HttpOptions options) {
        HttpDelete request = new HttpDelete(url);
        if(options == null){
            options = new HttpOptions() {};
        }
        return sendRequest(request, options);
    }
    
    /**
     * 配置请求头域
     * @param request 请求实例
     * @param options 配置实例
     */
    private void fillHeaders(HttpRequestBase request, HttpOptions options){
        Map<String, String> headers = options.getHeaders();
        for(Entry<String, String> entry : headers.entrySet()){
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 核心请求方法
     * @param request 请求对象，可以是get、post...
     * @return
     */
    private SimpleResponse sendRequest(HttpRequestBase request, HttpOptions options) {
        //响应对象
        CloseableHttpResponse response = null;
        //响应封装
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setCharset(options.getResponseBodyCharset());
        
        try {
            //填充请求头域
            fillHeaders(request, options);
            //执行请求
            response = getCloseableHttpClient().execute(request);
            //获取响应状态码
            simpleResponse.setCode(response.getStatusLine().getStatusCode());
            //获取响应header
            simpleResponse.setHeaders(response.getAllHeaders());
            //获取响应体
            HttpEntity entity = response.getEntity();
            if(entity != null){
                simpleResponse.setBody(EntityUtils.toByteArray(entity));
            }
        } catch (Exception e) {
            throw new SecurityException(e);
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (Exception e) {
                response = null;
            }
        }
        
        return simpleResponse;
    }
}
