package org.yangyuan.pay.core.common;

import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.bean.AliTrade;
import org.yangyuan.pay.bean.TradeStatus;
import org.yangyuan.pay.bean.WxTrade;
import org.yangyuan.pay.bean.common.Trade;
import org.yangyuan.pay.bean.common.TradeToken;
import org.yangyuan.pay.config.CommonConfig;
import org.yangyuan.pay.core.*;
import org.yangyuan.pay.exception.RefundException;
import org.yangyuan.pay.util.PayDateUtil;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * 支付统一调度器定义抽象实现
 * @author yangyuan
 * @date 2017年5月9日
 */
public abstract class AbstractPayManager implements PayManager{
    /**
     * 用户订单数量模板
     */
    private static final String TRADE_COUNT_KEY_TMPL = "service:pay:manager:trade_count:{type}:{uuid}";
    
    /**
     * 支付业务核心
     * <br>
     * 这里之所以用静态，是为了减少内存占用，这些核心类线程安全，在内存中只保留一份即可，即永久代中。
     */
    private static final AliMobilePay aliMobilePay = new AliMobilePay();
    private static final AliWebPcPay aliWebPcPay = new AliWebPcPay();
    private static final AliWebMobilePay aliWebMobilePay = new AliWebMobilePay();
    private static final AliQrcodePay aliQrcodePay = new AliQrcodePay();
    private static final WxMobilePay wxMobilePay = new WxMobilePay();
    private static final WxWebPcPay wxWebPcPay = new WxWebPcPay();
    private static final WxWebMobilePay wxWebMobilePay = new WxWebMobilePay();
    private static final WxQrcodePay wxQrcodePay = new WxQrcodePay();
    private static final WxJsSdkPay wxJsSdkPay = new WxJsSdkPay();

    /**
     * 获取一个新的<b>支付订单号</b>(注意<b>业务订单号</b>和<b>支付订单号</b>的概念区别！项目中所有的商户订单号均为支付订单号)
     * 
     * <p>格式定义：</p>
     * 
     * <pre>
     * 固定16位。
     * 假设<b>支付订单号</b>为：00ba00ix1sob0001
     * 
     * +------------------------------------+------------------+----------------+------------------------+--------------------+---------------------+----------------------------------+
     * | 00                                 | b                | a              | 0                      | 0                  | ix1sob              | 0001                             |
     * +------------------------------------+------------------+----------------+------------------------+--------------------+---------------------+----------------------------------+
     * | 从2017年起至现在的年数，32进制表示                      | 月份，32进制表示             | 日，32进制表示           | 订单号类型，32进制表示                | 保留位，32进制表示              | 用户唯一id，固定6位              | 该用户当日创建订单数量，32进制表示                    |
     * +------------------------------------+------------------+----------------+------------------------+--------------------+---------------------+----------------------------------+
     * </pre>
     * 
     * @param uuid 用户唯一id
     * 
     * @return 支付订单号
     */
    public String newTradeNo(String uuid){
        Jedis redis = null;
        try {
            redis = CommonConfig.getJedisPool().getResource();
            StringBuilder builder = new StringBuilder(16);
            Date date = new Date();
            
            String key = TRADE_COUNT_KEY_TMPL.replace("{type}", getTradeType())
                                             .replace("{uuid}", uuid);
            int count = redis.incr(key).intValue();
            if(count == 1){
                int ttl = (int)((PayDateUtil.getIntegralEndTime(date).getTime() - date.getTime())/1000l);
                redis.expire(key, ttl);
            }
            
            int year = Integer.parseInt(PayDateUtil.format(date, "yyyy"));
            year = year - 2017;
            int month = Integer.parseInt(PayDateUtil.format(date, "MM"));
            if(month > 9){
                month = month + 87;
            }else{
                month = month + 48;
            }
            int day = Integer.parseInt(PayDateUtil.format(date, "dd"));
            if(day > 9){
                day = day + 87;
            }else{
                day = day + 48;
            }
            builder.append(StringUtils.leftPad(Integer.toString(year, 32), 2, "0"));
            builder.append((char) month);
            builder.append((char) day);
            
            /**
             * 类型
             */
            builder.append(getTradeType());
            
            /**
             * 保留位
             */
            builder.append(0);
            
            /**
             * 用户唯一id
             */
            builder.append(uuid);
            
            /**
             * 数量
             */
            builder.append(StringUtils.leftPad(Integer.toString(count, 32), 4, "0"));
            
            return builder.toString();
        } finally {
            if(redis != null){
                redis.close();
                redis = null;
            }
        }
    }
    
    /**
     * 获取订单类型
     * <br>
     * 长度1位，32进制表示法，即：0~v
     * @return 订单类型
     */
    public abstract String getTradeType();
    
    /**
     * 获取当前支付调度器对应的支付适配器
     * @return
     */
    public abstract AbstractPayAdaptor getPayAdaptor();
    
    /**
     * 订单状态分发
     * @param status
     */
    public void doTradeStatus(TradeStatus status){
        getPayAdaptor().doTradeStatus(status);
    }
    
    /**
     * 匹配订单类型
     * @param outTradeNo 商户订单号
     * @return
     */
    public boolean matches(String outTradeNo){
        return outTradeNo.substring(4, 5).equals(getTradeType());
    }

    @Override
    public <T> TradeToken<T>  mobilePay(Trade trade) {
        if(trade instanceof AliTrade){
            return (TradeToken<T>) aliMobilePay.pay((AliTrade) trade);
        }

        if(trade instanceof WxTrade){
            return (TradeToken<T>) wxMobilePay.pay((WxTrade) trade);
        }

        throw new IllegalArgumentException("[移动支付]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public <T> TradeToken<T> webPcPay(Trade trade) {
        if(trade instanceof AliTrade){
            return (TradeToken<T>) aliWebPcPay.pay((AliTrade) trade);
        }

        if(trade instanceof WxTrade){
            return (TradeToken<T>) wxWebPcPay.pay((WxTrade) trade);
        }

        throw new IllegalArgumentException("[电脑网页支付]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public <T> TradeToken<T> webMobilePay(Trade trade) {
        if(trade instanceof AliTrade){
            return (TradeToken<T>) aliWebMobilePay.pay((AliTrade) trade);
        }

        if(trade instanceof WxTrade){
            return (TradeToken<T>) wxWebMobilePay.pay((WxTrade) trade);
        }

        throw new IllegalArgumentException("[手机网页支付]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public <T> TradeToken<T> qrcodePay(Trade trade) {
        if(trade instanceof AliTrade){
            return (TradeToken<T>) aliQrcodePay.pay((AliTrade) trade);
        }

        if(trade instanceof WxTrade){
            return (TradeToken<T>) wxQrcodePay.pay((WxTrade) trade);
        }

        throw new IllegalArgumentException("[扫码支付]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public <T> TradeToken<T> jsSdkPay(Trade trade) {
        if(trade instanceof AliTrade){
            throw new IllegalArgumentException("[JS-SDK支付]支付宝不支持该支付方式");
        }

        if(trade instanceof WxTrade){
            return (TradeToken<T>) wxJsSdkPay.pay((WxTrade) trade);
        }

        throw new IllegalArgumentException("[JS-SDK支付]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public void refund(Trade trade) throws RefundException {
        if(trade instanceof AliTrade){
            aliMobilePay.refund((AliTrade) trade);
            return;
        }

        if(trade instanceof WxTrade){
            wxMobilePay.refund((WxTrade) trade);
            return;
        }

        throw new IllegalArgumentException("[退款]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }

    @Override
    public TradeStatus status(Trade trade) {
        if(trade instanceof AliTrade){
            return aliMobilePay.status((AliTrade) trade);
        }

        if(trade instanceof WxTrade){
            return wxMobilePay.status((WxTrade) trade);
        }

        throw new IllegalArgumentException("[订单状态查询]不支持的订单类型[" + trade.getClass().getSimpleName() + "]");
    }
}
