package com.glmapper.simple.common;

/**
 * SimpleException
 *
 * @author: Jerry
 * @date: 2018/6/22
 */
public class SimpleException extends RuntimeException {

    private static final long serialVersionUID = 3552899395127322985L;

    public SimpleException() {
    }

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(String message, Throwable cause) {
        super(message, cause);
    }
}
