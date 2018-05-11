package io.dyakonoff.listenersvalidation.listener;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import io.dyakonoff.listenersvalidation.entity.Printer;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

@Component("listenersvalidation_PrinterEntityListener")
public class PrinterEntityListener implements BeforeInsertEntityListener<Printer>, BeforeUpdateEntityListener<Printer> {


    @Override
    public void onBeforeInsert(Printer entity, EntityManager entityManager) {

    }


    @Override
    public void onBeforeUpdate(Printer entity, EntityManager entityManager) {

    }


}