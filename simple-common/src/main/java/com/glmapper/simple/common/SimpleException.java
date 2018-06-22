package com.glmapper.simple.common;

/**
 * SimpleException
 *
 * @author: Jerry
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
