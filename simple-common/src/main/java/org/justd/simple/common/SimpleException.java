package org.justd.simple.common;

/**
 * @author Zhangjd
 * @title: SimpleException
 * @description:
 * @date 2020/1/321:26
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
