package io.dyakonoff.listenersvalidation.exception;

import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
public class PrinterValidationException extends RuntimeException {
    public PrinterValidationException() {
    }

    public PrinterValidationException(String message) {
        super(message);
    }

    public PrinterValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PrinterValidationException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
