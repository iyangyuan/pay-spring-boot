package org.yangyuan.pay.util;

import org.yangyuan.pay.config.CommonConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

/**
 * 字符串工具类
 * @author 杨元
 *
 */
public class PayStrUtil {
    
    private static char underscoreChar = "_".charAt(0);

    /**
     * 字符串不为null且不为空
     * 
     * @param str
     * @return
     */
    public static boolean isNotNullAndEmpty(String str) {
        return (str != null) && (!"".equals(str));
    }

    /**
     * 字符串为null或者为空
     * 
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return !isNotNullAndEmpty(str);
    }

    /**
     * 以双引号包裹字符串
     * 
     * @param str
     * @return
     */
    public static String wrapWithQuot(String str) {
        return "\"".concat(str).concat("\"");
    }

    /**
     * 获取指定长度的随机字符串
     * 
     * @param length
     * @return
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(32);
        
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        
        return sb.toString();
    }

    /**
     * 编码字符串
     * @param text 字符串
     * @return
     */
    public static String encodeURL(String text){
        try {
            return URLEncoder.encode(text, CommonConfig.UNIFY_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 解码字符串
     * @param text 字符串
     * @return
     */
    public static String decodeURL(String text){
        try {
            return URLDecoder.decode(text, CommonConfig.UNIFY_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 字符串转unicode编码
     * @param str
     * @return
     */
    public static String toUnicode(String str) {
        StringBuilder sb = new StringBuilder(64);

        for (int i = 0; i < str.length(); i++) {
            sb.append("\\u");
            sb.append(Integer.toHexString(str.charAt(i) & 0xffff));
        }

        return sb.toString();
    }

    /**
     * unicode编码转字符串
     * @param str
     * @return
     */
    public static String fromUnicode(String str) {
        StringBuilder sb = new StringBuilder(64);
        String[] wrods = str.split("\\\\u");
        
        for (int i = 1; i < wrods.length; i++) {
            sb.append((char) Integer.parseInt(wrods[i], 16));
        }
        
        return sb.toString();
    }
    
    /**
     * 将下划线命名转换成大驼峰命名
     * 
     * 注意：
     * 
     * 下划线命名不能以_开始
     * 下划线命名不能有连续的__
     * 
     * @param name
     * @return
     */
    public static String underscoreCaseToBigCamelCase(String name){
        StringBuilder sb = new StringBuilder(32);
        int i = 0;
        char c;
        
        while(i < name.length()){
            sb.append(String.valueOf(name.charAt(i)).toUpperCase());
            i++;
            for(; i < name.length(); i++){
                c = name.charAt(i);
                if(c == underscoreChar){
                    i++;
                    break;
                }else{
                    sb.append(c);
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 首字母小写
     * @param string
     * @return
     */
    public static String firstLetterToLower(String string) {
        char[] buffer = string.toCharArray();
        buffer[0] = Character.toLowerCase(string.charAt(0));
        return new String(buffer);
    }
    
    /**
     * 首字母大写
     * @param string
     * @return
     */
    public static String firstLetterToUpper(String string) {
        char[] buffer = string.toCharArray();
        buffer[0] = Character.toUpperCase(string.charAt(0));
        return new String(buffer);
    }
    
    /**
     * sql like 查询关键字处理
     * @param content
     * @return
     */
    public static String sqlLikeWrap(String content){
        //防止注入，过滤%
//        content = content.replace("%", "");
        
        return "%".concat(content).concat("%");
    }
    
    /**
     * 获取扩展名
     * @param name
     * @return
     */
    public static String getSuffix(String name){
        
        return name.substring(name.lastIndexOf(".") + 1, name.length());
        
    }
    
    /**
     * 驼峰命名缩写
     * ex: UserInfo --> ui
     * @param name
     * @return
     */
    public static String minName(String name){
        
        return name.replaceAll("[^A-Z]", "").toLowerCase();
        
    }
    
    /**
     * 获取相对路径
     * @param root 根路径
     * @param absolute 完整路径（绝对路径）
     * @return
     */
    public static String relativePath(String root, String absolute){
        int beginIndex = root.length();
        
        if(root.endsWith("/")){
            beginIndex = beginIndex - 1;
        }
        
        return absolute.substring(beginIndex);
    }
    
    /**
     * 构造绝对路径
     * @param root 根路径
     * @param relative 相对路径
     * @return
     */
    public static String absolutePath(String root, String relative){
        if(root.endsWith("/") && relative.startsWith("/")){
            return root.concat(relative.substring(1));
        }
        
        if(!root.endsWith("/") && !relative.startsWith("/")){
            return root.concat("/").concat(relative);
        }
        
        return root.concat(relative);
    }
    
    /**
     * 编码转换
     * @param str 字符串
     * @param source 原编码
     * @param target 新编码
     * @return
     */
    public static String transCharset(String str, String source, String target){
        try {
            return new String(str.getBytes(source), target);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 将字符串格式化为手机号码长度
     * @param str
     * @return
     */
    public static String format2Phone(String str){
        if(str.length() >= 11){
            throw new RuntimeException("字符串超长，长度应该小于11位");
        }
        
        StringBuilder sb = new StringBuilder(11);
        sb.append("A");
        
        int fixLength = 11 - str.length() - 1;
        for(int i = 0; i < fixLength; i++){
          sb.append("0");
        }
        
        sb.append(str);
        
        return sb.toString();
    }
    
    /**
     * 合并字符串
     * @param prefix
     * @param suffix
     * @return
     */
    public static String glue(String prefix, String suffix){
        if(prefix == null || suffix == null){
            return null;
        }
        
        return prefix + suffix;
    }
    
    /**
     * 合并字符串
     * @param prefix
     * @param suffixes
     * @return
     */
    public static String[] glueAll(String prefix, String[] suffixes){
        if(prefix == null || suffixes == null){
            return new String[0];
        }
        
        String[] result = new String[suffixes.length];
        
        for(int i = 0; i < suffixes.length; i++){
            result[i] = prefix + suffixes[i];
        }
        
        return result;
    }
    
    
    
    
    
    
    
    
    
}
