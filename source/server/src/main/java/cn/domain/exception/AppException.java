package cn.domain.exception;

/**
 * @author ZuoBro
 * date: 2021/1/31
 * time: 12:07
 */
public class AppException extends RuntimeException{
    /**
     * 错误码
     */
    private final String errorCode;

    public AppException(String message) {
        super(message);
        this.errorCode = "500";
    }

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public AppException(String errorCode, String message, Throwable cause) {
        super(message,cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {return this.errorCode;}
}
