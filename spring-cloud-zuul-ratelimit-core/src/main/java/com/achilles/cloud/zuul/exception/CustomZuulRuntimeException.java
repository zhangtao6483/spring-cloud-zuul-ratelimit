package com.achilles.cloud.zuul.exception;

import com.netflix.zuul.monitoring.CounterFactory;

/**
 * @author zhangtao
 * @date 2017/7/26.
 */
public class CustomZuulRuntimeException extends RuntimeException {
    public int nStatusCode;
    public String errorCause;

    public CustomZuulRuntimeException(Throwable throwable, String sMessage, int nStatusCode, String errorCause) {
        super(sMessage, throwable);
        this.nStatusCode = nStatusCode;
        this.errorCause = errorCause;
        incrementCounter("ZUUL::RUNTIME EXCEPTION:" + errorCause + ":" + nStatusCode);
    }

    public CustomZuulRuntimeException(String sMessage, int nStatusCode, String errorCause) {
        super(sMessage);
        this.nStatusCode = nStatusCode;
        this.errorCause = errorCause;
        incrementCounter("ZUUL::RUNTIME EXCEPTION:" + errorCause + ":" + nStatusCode);
    }

    public CustomZuulRuntimeException(Throwable throwable, int nStatusCode, String errorCause) {
        super(throwable.getMessage(), throwable);
        this.nStatusCode = nStatusCode;
        this.errorCause = errorCause;
        incrementCounter("ZUUL::RUNTIME EXCEPTION:" + errorCause + ":" + nStatusCode);
    }

    private static final void incrementCounter(String name) {
        CounterFactory.instance().increment(name);
    }
}
