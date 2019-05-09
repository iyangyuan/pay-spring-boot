package org.yangyuan.pay.exception;

/**
 * 退款异常
 * @Auther: yangyuan
 * @Date: 2019/1/4 15:27
 */
public class RefundException extends RuntimeException{
    public RefundException() {
    }

    public RefundException(String message) {
        super(message);
    }

    public RefundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefundException(Throwable cause) {
        super(cause);
    }

    public RefundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
