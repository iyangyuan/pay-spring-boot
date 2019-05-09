package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.TransferTrade;
import org.yangyuan.pay.config.AliPayConfig;
import org.yangyuan.pay.config.CommonConfig;

/**
 * 支付宝转账订单
 * @author yangyuan
 * @date 2017年9月29日
 */
public class AliTransferTrade implements TransferTrade {
    
    private AliTransferTrade(Builder builder){
        this.appid = builder.appid;
        this.method = builder.method;
        this.format = builder.format;
        this.charset = builder.charset;
        this.timestamp = builder.timestamp;
        this.version = builder.version;
        this.outBizNo = builder.outBizNo;
        this.payeeType = builder.payeeType;
        this.payeeAccount = builder.payeeAccount;
        this.amount = builder.amount;
        this.payerShowName = builder.payerShowName;
        this.payeeRealName = builder.payeeRealName;
        this.remark = builder.remark;
    }
    
    private final String appid;
    private final String method;
    private final String format;
    private final String charset;
    private final String timestamp;
    private final String version;
    private final String outBizNo;
    private final String payeeType;
    private final String payeeAccount;
    private final String amount;
    private final String payerShowName;
    private final String payeeRealName;
    private final String remark;
    
    public String getAppid() {
        return appid;
    }

    public String getMethod() {
        return method;
    }

    public String getFormat() {
        return format;
    }

    public String getCharset() {
        return charset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public String getPayeeType() {
        return payeeType;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayerShowName() {
        return payerShowName;
    }

    public String getPayeeRealName() {
        return payeeRealName;
    }

    public String getRemark() {
        return remark;
    }

    public static class Builder{
        
        /**
         * 公共请求参数
         */
        
        /**
         * 支付宝分配给开发者的应用ID
         * <p><b>example:</b></p>
         * <p>2014072300007148</p>
         */
        private String appid;
        /**
         * 接口名称
         * <p><b>example:</b></p>
         * <p>alipay.fund.trans.toaccount.transfer</p>
         */
        private String method;
        /**
         * 仅支持JSON
         * <p><b>example:</b></p>
         * <p>JSON</p>
         */
        private String format = "JSON";
        /**
         * 请求使用的编码格式，如utf-8,gbk,gb2312等
         * <p><b>example:</b></p>
         * <p>utf-8</p>
         */
        private String charset = CommonConfig.UNIFY_CHARSET;
        /**
         * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
         * <p><b>example:</b></p>
         * <p>2014-07-24 03:07:50</p>
         */
        private String timestamp;
        /**
         * 调用的接口版本，固定为：1.0
         * <p><b>example:</b></p>
         * <p>1.0</p>
         */
        private String version = "1.0";
        
        /**
         * 业务请求参数
         */
        
        /**
         * 商户转账唯一订单号。发起转账来源方定义的转账单据ID，用于将转账回执通知给来源方。 不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。 只支持半角英文、数字，及“-”、“_”。
         * <p><b>example:</b></p>
         * <p>3142321423432</p>
         */
        private String outBizNo;
        
        /**
         * 收款方账户类型。可取值： 1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。 2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
         * <p><b>example:</b></p>
         * <p>ALIPAY_LOGONID</p>
         */
        private String payeeType;
        
        /**
         * 收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户。
         * <p><b>example:</b></p>
         * <p>abc@sina.com</p>
         */
        private String payeeAccount;
        
        /**
         * 转账金额，单位：元。 只支持2位小数，小数点前最大支持13位，金额必须大于等于0.1元。 最大转账金额以实际签约的限额为准。
         * <p><b>example:</b></p>
         * <p>12.23</p>
         */
        private String amount;
        
        /**
         * 付款方姓名（最长支持100个英文/50个汉字）。显示在收款方的账单详情页。如果该字段不传，则默认显示付款方的支付宝认证姓名或单位名称。
         * <p><b>example:</b></p>
         * <p>上海交通卡退款</p>
         */
        private String payerShowName;
        
        /**
         * 收款方真实姓名（最长支持100个英文/50个汉字）。 如果本参数不为空，则会校验该账户在支付宝登记的实名是否与收款方真实姓名一致。
         * <p><b>example:</b></p>
         * <p>张三</p>
         */
        private String payeeRealName;
        
        /**
         * 转账备注（支持200个英文/100个汉字）。 当付款方为企业账户，且转账金额达到（大于等于）50000元，remark不能为空。收款方可见，会展示在收款用户的收支详情中。
         * <p><b>example:</b></p>
         * <p>转账备注</p>
         */
        private String remark;

        public Builder appid(String appid) {
            this.appid = appid;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder outBizNo(String outBizNo) {
            this.outBizNo = outBizNo;
            return this;
        }

        public Builder payeeType(String payeeType) {
            this.payeeType = payeeType;
            return this;
        }

        public Builder payeeAccount(String payeeAccount) {
            this.payeeAccount = payeeAccount;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder payerShowName(String payerShowName) {
            this.payerShowName = payerShowName;
            return this;
        }

        public Builder payeeRealName(String payeeRealName) {
            this.payeeRealName = payeeRealName;
            return this;
        }

        public Builder remark(String remark) {
            this.remark = remark;
            return this;
        }
        
        public AliTransferTrade build(){
            return new AliTransferTrade(this);
        }
        
        /**
         * 通用构造器
         * @return
         */
        public static Builder basic(){
            Builder builder = new Builder();
            
            return builder.appid(AliPayConfig.getAppid());
        }
        
        /**
         * 转账构造器
         * <br>
         * 默认使用支付宝登陆账号转账
         * @return
         */
        public static Builder transfer(){
            return basic().method("alipay.fund.trans.toaccount.transfer")
                            .payeeType("ALIPAY_LOGONID");
        }
        
        /**
         * 转账查询构造器
         * @return
         */
        public static Builder status(){
            return basic().method("alipay.fund.trans.order.query");
        }
        
    } 
    
    
    
    
}
