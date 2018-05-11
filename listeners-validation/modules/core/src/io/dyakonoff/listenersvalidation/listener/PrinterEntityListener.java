package io.dyakonoff.listenersvalidation.listener;

import io.dyakonoff.listenersvalidation.exception.PrinterValidationException;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import io.dyakonoff.listenersvalidation.entity.Printer;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

import javax.inject.Inject;

@Component("listenersvalidation_PrinterEntityListener")
public class PrinterEntityListener implements BeforeInsertEntityListener<Printer>, BeforeUpdateEntityListener<Printer> {

    @Inject
    private IpAddressCheckerService ipAddressCheckerService;

    @Override
    public void onBeforeInsert(Printer entity, EntityManager entityManager) {
        checkPrinterIsReachable(entity);
    }

    @Override
    public void onBeforeUpdate(Printer entity, EntityManager entityManager) {
        checkPrinterIsReachable(entity);
    }

    private void checkPrinterIsReachable(Printer printer) {
        String ipAddr = printer.getIpAddress();
        if (!ipAddressCheckerService.checkIpAddrIsReacheble(ipAddr, 2000)) {
            throw new PrinterValidationException("Printer at " + ipAddr + " is not reachable");
        }
    }
}