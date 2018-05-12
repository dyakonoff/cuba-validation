package io.dyakonoff.listenersvalidation.listener;

import com.haulmont.cuba.core.PersistenceTools;
import io.dyakonoff.listenersvalidation.entity.PrintJob;
import io.dyakonoff.listenersvalidation.entity.Printer;
import io.dyakonoff.listenersvalidation.exception.PrintJobValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeCommitTransactionListener;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.entity.Entity;

import javax.inject.Inject;
import java.util.Collection;

@Component("listenersvalidation_TransactionListener")
public class TransactionListener implements BeforeCommitTransactionListener {

    private Logger log = LoggerFactory.getLogger(TransactionListener.class);

    @Inject
    private PersistenceTools persistenceTools;

    @Inject
    private IpAddressCheckerService ipAddressCheckerService;

    @Override
    public void beforeCommit(EntityManager entityManager, Collection<Entity> managedEntities) {
        for (Entity entity : managedEntities) {
            if (!persistenceTools.isDirty(entity))
                continue;

            if (entity instanceof  PrintJob) {
                PrintJob pj = (PrintJob)entity;
                Printer printer = pj.getPrinter();
                if ((pj.getPrintOnBothSides() != null && pj.getPrintOnBothSides())
                        && (printer.getDuplexSupport() == null || !printer.getDuplexSupport())
                        ) {
                    String msg = "File " + pj.getFile().getName() + " can't be printed on printer " + printer.getName() +
                            ", this printer does not support duplex printing";
                    throw new PrintJobValidationException(msg);

                }
            }
        }
    }
}