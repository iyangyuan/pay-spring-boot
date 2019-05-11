package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.TransferTrade;
import org.yangyuan.pay.config.WxPayConfig;
import org.yangyuan.pay.util.PayMD5;

import java.util.UUID;

/**
 * 微信转账订单
 * @author yangyuan
 * @date 2017年10月16日
 */
public class WxTransferTrade implements TransferTrade {
    
    private final String appid;
    private final String mchid;
    private final String deviceInfo;
    private final String nonceStr;
    private final String openid;
    private final String partnerTradeNo;
    private final String checkName;
    private final String reUserName;
    private final String amount;
    private final String desc;
    private final String spbillCreateIp;
    
    private WxTransferTrade(Builder builder){
        this.appid = builder.appid;
        this.mchid = builder.mchid;
        this.deviceInfo = builder.deviceInfo;
        this.nonceStr = builder.nonceStr;
        this.partnerTradeNo = builder.partnerTradeNo;
        this.openid = builder.openid;
        this.checkName = builder.checkName;
        this.reUserName = builder.reUserName;
        this.amount = builder.amount;
        this.desc = builder.desc;
        this.spbillCreateIp = builder.spbillCreateIp;
    }
    
    public String getOpenid() {
        return openid;
    }

    public String getAppid() {
        return appid;
    }

    public String getMchid() {
        return mchid;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getPartnerTradeNo() {
        return partnerTradeNo;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getReUserName() {
        return reUserName;
    }

    public String getAmount() {
        return amount;
    }

    public String getDesc() {
        return desc;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public static class Builder{
        /**
         * 微信分配的账号ID（企业号corpid即为此appId）
         * <p><b>example:</b></p>
         * <p>wx8888888888888888</p>
         */
        private String appid;
        
        /**
         * 微信支付分配的商户号
         * <p><b>example:</b></p>
         * <p>1900000109</p>
         */
        private String mchid;
        
        /**
         * 微信支付分配的终端设备号
         * <p><b>example:</b></p>
         * <p>013467007045764</p>
         */
        private String deviceInfo;
        
        /**
         * 随机字符串，不长于32位
         * <p><b>example:</b></p>
         * <p>5K8264ILTKCH16CQ2502SI8ZNMTM67VS</p>
         */
        private String nonceStr;
        
        /**
         * 商户订单号，需保持唯一性(只能是字母或者数字，不能包含有符号)
         * <p><b>example:</b></p>
         * <p>10000098201411111234567890</p>
         */
        private String partnerTradeNo;
        
        /**
         * 商户appid下，某用户的openid
         * <p><b>example:</b></p>
         * <p>oxTWIuGaIt6gTKsQRLau2M0yL16E</p>
         */
        private String openid;
        
        /**
         * NO_CHECK：不校验真实姓名，FORCE_CHECK：强校验真实姓名
         * <p><b>example:</b></p>
         * <p>FORCE_CHECK</p>
         */
        private String checkName;
        
        /**
         * 收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
         * <p><b>example:</b></p>
         * <p>王小王</p>
         */
        private String reUserName;
        
        /**
         * 企业付款金额，单位为分
         * <p><b>example:</b></p>
         * <p>10099</p>
         */
        private String amount;
        
        /**
         * 企业付款操作说明信息。必填。
         * <p><b>example:</b></p>
         * <p>理赔</p>
         */
        private String desc;
        
        /**
         * 调用接口的机器Ip地址
         * <p><b>example:</b></p>
         * <p>192.168.0.1</p>
         */
        private String spbillCreateIp;

        public Builder appid(String appid) {
            this.appid = appid;
            return this;
        }

        public Builder mchid(String mchid) {
            this.mchid = mchid;
            return this;
        }

        public Builder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public Builder nonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public Builder openid(String openid) {
            this.openid = openid;
            return this;
        }

        public Builder partnerTradeNo(String partnerTradeNo) {
            this.partnerTradeNo = partnerTradeNo;
            return this;
        }

        public Builder checkName(String checkName) {
            this.checkName = checkName;
            return this;
        }

        public Builder reUserName(String reUserName) {
            this.reUserName = reUserName;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder spbillCreateIp(String spbillCreateIp) {
            this.spbillCreateIp = spbillCreateIp;
            return this;
        }
        
        /**
         * 构造订单
         * @return
         */
        public WxTransferTrade build(){
            return new WxTransferTrade(this);
        }

    }

    /**
     * 自定义构造器
     * @return
     */
    public static Builder custom(){
        Builder builder = new Builder();

        return builder.appid(WxPayConfig.getAppid())
                .mchid(WxPayConfig.getMchid())
                .nonceStr(PayMD5.encode(UUID.randomUUID().toString()));
    }

    /**
     * 转账构造器
     * @return
     */
    public static Builder transfer(){
        return custom().checkName("NO_CHECK");
    }

}
