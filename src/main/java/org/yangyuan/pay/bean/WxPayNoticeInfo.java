package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.NoticeInfo;

import java.util.Date;

/**
 * 微信支付回调信息
 * @author yangyuan
 * @date 2017年5月8日
 */
public class WxPayNoticeInfo implements NoticeInfo {
    /**
     * 公众账号ID
     */
    private String appid;
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 设备号
     */
    private String deviceInfo;
    
    /**
     * 随机字符串
     */
    private String nonceStr;
    
    /**
     * 签名
     */
    private String sign;
    
    /**
     * 业务结果
     */
    private String resultCode;
    
    /**
     * 错误代码
     */
    private String errCode;
    
    /**
     * 错误代码描述
     */
    private String errCodeDes;
    
    /**
     * 用户标识
     */
    private String openid;
    
    /**
     * 是否关注公众账号
     */
    private String isSubscribe;
    
    /**
     * 交易类型
     */
    private String tradeType;
    
    /**
     * 付款银行
     */
    private String bankType;
    
    /**
     * 总金额(单位为分)
     */
    private int totalFee;
    
    /**
     * 货币种类
     */
    private String feeType;
    
    /**
     * 现金支付金额
     */
    private int cashFee;
    
    /**
     * 现金支付货币类型
     */
    private String cashFeeType;
    
    /**
     * 代金券或立减优惠金额
     */
    private int couponFee;
    
    /**
     * 代金券或立减优惠使用数量
     */
    private int couponCount;
    
    //代金券或立减优惠批次ID
    //代金券或立减优惠ID
    //单个代金券或立减优惠支付金额
    
    /**
     * 微信支付订单号
     */
    private String transactionId;
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 商家数据包
     */
    private String attach;
    
    /**
     * 支付完成时间
     * 格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。
     */
    private String timeEnd;
    
    /**
     * 创建时间
     */
    private Date createTime;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public int getCashFee() {
        return cashFee;
    }

    public void setCashFee(int cashFee) {
        this.cashFee = cashFee;
    }

    public String getCashFeeType() {
        return cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    public int getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(int couponFee) {
        this.couponFee = couponFee;
    }

    public int getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(int couponCount) {
        this.couponCount = couponCount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
