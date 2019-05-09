package org.yangyuan.pay.bean;

import org.yangyuan.pay.bean.common.NoticeInfo;

import java.util.Date;

/**
 * 支付宝支付回调信息
 * @author yangyuan
 * @date 2017年5月8日
 */
public class AliPayNoticeInfo implements NoticeInfo {
    /*
        公共参数
     */
    
    /**
     * 通知时间
     */
    private Date notifyTime;
    
    /**
     * 通知类型
     */
    private String notifyType;
    
    /**
     * 通知校验ID
     */
    private String notifyId;

    /**
     * 支付宝分配给开发者的应用Id
     */
    private String appId;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * 编码格式
     */
    private String version;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * 签名
     */
    private String sign;

    /*
        业务参数
     */
    
    /**
     * 商户网站唯一订单号
     * 需保证在商户网站中的唯一性。是请求时对应的参数，原样返回。
     */
    private String outTradeNo;
    
    /**
     * 商品名称
     */
    private String subject;
    
    /**
     * 该交易在支付宝系统中的交易流水号。
     */
    private String tradeNo;

    /**
     * 商户业务号
     */
    private String outBizNo;

    /**
     * 交易状态
     */
    private String tradeStatus;
    
    /**
     * 卖家支付宝用户号
     */
    private String sellerId;
    
    /**
     * 卖家支付宝帐号
     */
    private String sellerEmail;
    
    /**
     * 买家支付宝用户号
     */
    private String buyerId;

    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;

    /**
     * 交易金额
     */
    private double totalAmount;

    /**
     * 实收金额
     */
    private double receiptAmount;

    /**
     * 开票金额
     */
    private double invoiceAmount;

    /**
     * 付款金额
     */
    private double buyerPayAmount;

    /**
     * 集分宝金额
     */
    private double pointAmount;

    /**
     * 总退款金额
     */
    private double refundFee;

    /**
     * 商品描述
     */
    private String body;
    
    /**
     * 交易创建时间
     */
    private Date gmtCreate;
    
    /**
     * 交易付款时间
     */
    private Date gmtPayment;

    /**
     * 支付金额信息
     */
    private String fundBillList;

    /**
     * 回传参数
     */
    private String passbackParams;

    /**
     * 优惠券信息
     */
    private String voucherDetailList;

    /**
     * 退款时间
     */
    private Date gmtRefund;

    /**
     * 交易结束时间
     */
    private Date gmtClose;

    /**
     * 创建时间
     */
    private Date createTime;

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public double getBuyerPayAmount() {
        return buyerPayAmount;
    }

    public void setBuyerPayAmount(double buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }

    public double getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(double pointAmount) {
        this.pointAmount = pointAmount;
    }

    public double getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(double refundFee) {
        this.refundFee = refundFee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    public String getFundBillList() {
        return fundBillList;
    }

    public void setFundBillList(String fundBillList) {
        this.fundBillList = fundBillList;
    }

    public String getPassbackParams() {
        return passbackParams;
    }

    public void setPassbackParams(String passbackParams) {
        this.passbackParams = passbackParams;
    }

    public String getVoucherDetailList() {
        return voucherDetailList;
    }

    public void setVoucherDetailList(String voucherDetailList) {
        this.voucherDetailList = voucherDetailList;
    }

    public Date getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(Date gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    public Date getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(Date gmtClose) {
        this.gmtClose = gmtClose;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
