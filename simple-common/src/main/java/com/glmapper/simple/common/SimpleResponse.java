package com.glmapper.simple.common;

import java.io.Serializable;

/**
 * RPC响应
 *
 * @author Jerry
 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = 2173375789723685184L;

    private String requestId;

    private Throwable error;

    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
