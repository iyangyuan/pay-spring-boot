package org.yangyuan.pay.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.config.CommonConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Pattern;


public class PayHttpUtil {
    
    //验证ip地址
    //[1-2]?[0-9]?[0-9]?\.[1-2]?[0-9]?[0-9]?\.[1-2]?[0-9]?[0-9]?\.[1-2]?[0-9]?[0-9]?
    private static final Pattern IP_PATTERN = Pattern.compile("[1-2]?[0-9]?[0-9]?\\.[1-2]?[0-9]?[0-9]?\\.[1-2]?[0-9]?[0-9]?\\.[1-2]?[0-9]?[0-9]?");

    /**
     * 重定向
     * @param response
     * @param location
     */
    public static void responserRedirect(HttpServletResponse response, String location){
        response.setHeader("Location", location);
        response.setStatus(302);
    }

    /**
     * 响应json数据
     * 
     * @param response
     *            响应对象
     * @param object
     *            响应消息体
     */
    public static void responseJson(HttpServletResponse response, Object object) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getOutputStream().write(JSON.toJSONString(object).getBytes(Charset.forName(CommonConfig.UNIFY_CHARSET)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 响应二进制数据
     * 
     * @param response
     *            响应对象
     * @param fileName
     *            文件名称
     * @param file
     *            文件字节数组
     */
    public static void responseFile(HttpServletResponse response, String fileName, byte[] file) {
        try {
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Disposition", attachmentContentDispositionHeader(fileName));
            response.getOutputStream().write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将map集合中的参数按照url params参数格式拼接到url中
     *
     * @param params 参数集合
     * @param url 请求路径
     * @return
     */
    public static String appendParamsToURL(Map<String, String> params,
                                           String url) {
        try {
            StringBuilder sb = new StringBuilder(64);

            if(StringUtils.isNotBlank(url)) {
                sb.append(url);
                sb.append("?");
            }

            for (String key : params.keySet()) {
                sb.append(key);
                sb.append("=");
                sb.append(URLEncoder.encode(params.get(key), CommonConfig.UNIFY_CHARSET));
                sb.append("&");
            }

            return sb.toString().replaceAll("(&|\\?)$", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造http表单消息体
     * 
     * @param map
     *            参数集合
     * @return
     */
    public static String buildFormBody(Map<String, String> map) {
        StringBuilder sb = new StringBuilder(128);
        int mark = 0;

        try {
            if (map != null) {
                for (String key : map.keySet()) {
                    mark++;
                    sb.append(key);
                    sb.append("=");
                    sb.append(URLEncoder.encode(map.get(key), CommonConfig.UNIFY_CHARSET));
                    if (mark != map.keySet().size()) {
                        sb.append("&");
                    }
                }
            }

            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 设置cookie
     * 
     * @param response
     *            响应对象
     * @param name
     *            cookie名称
     * @param value
     *            cookie值
     */
    public static void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(7200);
        response.addCookie(cookie);
    }

    /**
     * 获取cookie
     * 
     *            响应对象
     * @param name
     *            cookie名称
     * @return null cookie不存在<br>
     *         空 cookie值为空<br>
     *         其他正常情况
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 文件下载ContentDisposition头域构造
     * 
     * @param fileName 下载文件名称
     * 
     * @return ContentDisposition头域(兼容IE)
     */
    public static String attachmentContentDispositionHeader(String fileName) {
        String headerValue = "attachment;";
        headerValue += " filename=\"" + encodeURIComponent(fileName) +"\";";
        headerValue += " filename*=utf-8''" + encodeURIComponent(fileName);
        
        return headerValue;
    }
    
    /**
     * 符合 RFC 3986 标准的“百分号URL编码”
     * 
     * <pre>
     * 在这个方法里，空格会被编码成%20，而不是+
     * 和浏览器的encodeURIComponent行为一致
     * </pre>
     * 
     * @param str 目标字符串
     * @return 编码后的字符串
     */
    public static String encodeURIComponent(String str) {
        try {
            return URLEncoder.encode(str, CommonConfig.UNIFY_CHARSET).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取客户端真实ip地址
     * @param request
     * @return 如果获取不到，默认返回127.0.0.1
     */
    public static String getRealClientIp(HttpServletRequest request) {
        try{
            String ip = request.getHeader("X-Forwarded-For");
            if(StringUtils.isNotBlank(ip)){
                //多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = ip.indexOf(",");
                String _ip;
                if(index != -1){
//                    log.info(":::获取客户端真实ip地址:::".concat(ip.substring(0,index)));
                    _ip = ip.substring(0,index);
                }else{
//                    log.info(":::获取客户端真实ip地址:::".concat(ip));
                    _ip = ip;
                }
                if(IP_PATTERN.matcher(_ip).matches()){
                    return _ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if(StringUtils.isNotBlank(ip) && IP_PATTERN.matcher(ip).matches()){
//                log.info(":::获取客户端真实ip地址:::".concat(ip));
                return ip;
            }
//            log.info(":::获取客户端真实ip地址:::".concat(request.getRemoteAddr()));
            return request.getRemoteAddr();
        }catch(Exception ex){
//            log.error(":::获取客户端真实ip地址异常:::", ex);
        }

//        log.info(":::获取客户端真实ip地址:::".concat("127.0.0.1"));
        return "127.0.0.1";
    }
    
}
