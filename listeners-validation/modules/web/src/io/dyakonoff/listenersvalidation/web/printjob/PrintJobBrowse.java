package io.dyakonoff.listenersvalidation.web.printjob;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import io.dyakonoff.listenersvalidation.entity.PrintJob;

public class PrintJobBrowse extends EntityCombinedScreen {
    @Override
    protected void initNewItem(Entity item) {
        PrintJob printJob = (PrintJob)item;
        if (printJob.getCopiesCount() == null) {
            printJob.setCopiesCount(1);
        }
        super.initNewItem(item);
    }
}