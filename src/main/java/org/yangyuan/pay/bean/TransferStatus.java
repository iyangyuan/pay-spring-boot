package org.yangyuan.pay.bean;

/**
 * 转账状态
 * @author yangyuan
 * @date 2019/1/4
 */
public class TransferStatus {
    private static final int UNKNOWN = 0;               //未知
    private static final int TRANSFER_SUCCESS = 1;      //转账成功
    private static final int TRANSFER_FAIL = 2;         //转账失败
    
    private TransferStatus(int status, String tradeNo){
        this.status = status;
        this.tradeNo = tradeNo;
    }

    /**
     * 订单状态
     */
    private final int status;

    /**
     * 商户订单号
     */
    private final String tradeNo;

    /**
     * 未知转账状态
     * @return
     */
    public boolean isUnknown(){
        return this.status == UNKNOWN;
    }

    /**
     * 转账是否成功
     * @return
     */
    public boolean isSuccess(){
        return this.status == TRANSFER_SUCCESS;
    }

    /**
     * 转账是否失败
     * @return
     */
    public boolean isFail(){
        return this.status == TRANSFER_FAIL;
    }
    
    /**
     * 转账成功状态
     * @param tradeNo 商户订单号
     * @return
     */
    public static TransferStatus successStatus(String tradeNo){
        return new TransferStatus(TRANSFER_SUCCESS, tradeNo);
    }
    
    /**
     * 转账失败状态
     * @return
     */
    public static TransferStatus failStatus(String tradeNo){
        return new TransferStatus(TRANSFER_FAIL, tradeNo);
    }

    /**
     * 未知转账状态
     * @return
     */
    public static TransferStatus unknownStatus(String tradeNo){
        return new TransferStatus(UNKNOWN, tradeNo);
    }

    public String getTradeNo() {
        return tradeNo;
    }
}
