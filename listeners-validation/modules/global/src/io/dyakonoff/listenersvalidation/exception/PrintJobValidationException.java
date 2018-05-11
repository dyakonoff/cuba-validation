package io.dyakonoff.listenersvalidation.exception;

import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
public class PrintJobValidationException extends RuntimeException {
    public PrintJobValidationException() {
    }

    public PrintJobValidationException(String message) {
        super(message);
    }

    public PrintJobValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PrintJobValidationException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
