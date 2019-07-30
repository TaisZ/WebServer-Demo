package com.webserver.exception;

/**
 * 空请求异常
 * 当HttpRequest解析请求时发现本次请求时空请求时会抛出异常
 */
public class EmptyRequestException extends Exception{

    private static final long serialVersionUID= 1L;
    public EmptyRequestException() {
        super();
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    public EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
