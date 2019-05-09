package org.yangyuan.pay.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * map工具类
 * @author 杨元
 *
 */
public class PayMapUtil {
    
    /**
     * 将一个map复制到另一个map中
     * @param source 源map
     * @param target 目标map
     */
    public static <K, V> void copyTo(Map<K, V> source, Map<K, V> target){
        Set<Entry<K, V>> entrySet = source.entrySet();
        Iterator<Entry<K, V>> iterator = entrySet.iterator();
        Entry<K, V> entry;
        while(iterator.hasNext()){
            entry = iterator.next();
            target.put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 返回一个新的map，并且过滤掉指定的属性
     * @param cls map泛型
     * @param map 要过滤的map
     * @param keys 要过滤的属性集合
     * @return
     * @throws Exception
     */
    public static <T> T filter(Class<? extends Map> cls, Map map, List<Object> keys){
        try{
            Map t = cls.newInstance();
            
            for(Object key : map.keySet()){
                if(!keys.contains(key)){
                    t.put(key, map.get(key));
                }
            }
            
            return (T) t;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取map的文本内容字符串格式
     * @param map
     * @return 不存在默认返回空字符串
     */
    public static String getStringText(Map<String,String> map, String key){
        
        return (null != map && PayStrUtil.isNotNullAndEmpty(map.get(key))) ? map.get(key) : "";
        
    }
    
    /**
     * 获取map的文本内容整型格式
     * @param map
     * @return 不存在默认返回0
     */
    public static int getIntegerText(Map<String,String> map, String key){
        
        return (null != map && PayStrUtil.isNotNullAndEmpty(map.get(key))) ? Integer.parseInt(map.get(key)) : 0;
        
    }
    
    /**
     * 获取map的文本内容长整型格式
     * @param map
     * @return 不存在默认返回0
     */
    public static long getLongText(Map<String,String> map, String key){
        
        return (null != map && PayStrUtil.isNotNullAndEmpty(map.get(key))) ? Long.parseLong(map.get(key)) : 0;
        
    }
    
    /**
     * 获取map的文本内容浮点型格式
     * @param map
     * @return 不存在默认返回0
     */
    public static double getDoubleText(Map<String,String> map, String key){
        
        return (null != map && PayStrUtil.isNotNullAndEmpty(map.get(key))) ? Double.parseDouble(map.get(key)) : 0.0d;
        
    }
    
    /**
     * 获取map的文本内容时间格式
     * @param map
     * @return 如果map为null或者值为null，返回null
     */
    public static Date getDateText(Map<String,String> map, String key){
        
        return (null != map && PayStrUtil.isNotNullAndEmpty(map.get(key))) ? PayDateUtil.parse(map.get(key)) : null;
        
    }
    
    
    
}
