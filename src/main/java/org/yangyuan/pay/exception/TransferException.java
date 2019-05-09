package org.yangyuan.pay.exception;

/**
 * 转账异常
 * @Auther: yangyuan
 * @Date: 2019/1/4 15:27
 */
public class TransferException extends RuntimeException{
    public TransferException() {
    }

    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferException(Throwable cause) {
        super(cause);
    }

    public TransferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
