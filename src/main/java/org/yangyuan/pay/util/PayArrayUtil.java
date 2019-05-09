package org.yangyuan.pay.util;

import java.util.ArrayList;
import java.util.List;

public class PayArrayUtil {
    
    /**
     * 通过指定的分隔符对列表进行分隔，生成一个字符串
     * @param list 列表
     * @param separator 分隔符
     * @return
     */
    public static String join(List<?> list, String separator){
        StringBuilder builder = new StringBuilder(128);
        
        int limit = list.size() - 1;
        for(int i = 0; i < list.size(); i++){
            builder.append(list.get(i));
            if(i != limit){
                builder.append(separator);
            }
        }
        
        return builder.toString();
    }
    
    /**
     * 列表去重，保留原有顺序，优先移除末尾重复元素
     * @param list
     */
    public static <T> void distinct(List<T> list){
        List<Integer> indexs = new ArrayList<Integer>();
        
        T t = null;
        for(int i = list.size() - 1; i > -1; i--){
            t = list.get(i);
            if(i == list.indexOf(t)){
                continue;
            }
            indexs.add(i);
        }
        
        for(Integer i : indexs){
            list.remove(i.intValue());
        }
    }
    
}
