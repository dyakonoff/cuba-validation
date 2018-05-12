package io.dyakonoff.listenersvalidation.exception;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component("listenersvalidation_PrintingValidationExceptionHandler")
public class PrintingValidationExceptionHandler extends AbstractGenericExceptionHandler {

    public PrintingValidationExceptionHandler() {
        super(PrintJobValidationException.class.getName(), PrinterValidationException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message, Frame.NotificationType.ERROR);
    }
}
