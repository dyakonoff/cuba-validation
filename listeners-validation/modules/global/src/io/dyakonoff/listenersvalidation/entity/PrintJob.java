package io.dyakonoff.listenersvalidation.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s * %s|file,copiesCount")
@Table(name = "LISTENERSVALIDATION_PRINT_JOB")
@Entity(name = "listenersvalidation$PrintJob")
public class PrintJob extends StandardEntity {
    private static final long serialVersionUID = 7930152096254299038L;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRINTER_ID")
    protected Printer printer;

    @Max(message = "Can't print more than 1000 copies", value = 1000)
    @Min(message = "Number of copies can not be smaller than 1", value = 1)
    @NotNull
    @Column(name = "COPIES_COUNT", nullable = false)
    protected Integer copiesCount;

    @Column(name = "PRINT_ON_BOTH_SIDES")
    protected Boolean printOnBothSides;

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setCopiesCount(Integer copiesCount) {
        this.copiesCount = copiesCount;
    }

    @Default("1")
    public Integer getCopiesCount() {
        return copiesCount;
    }

    public void setPrintOnBothSides(Boolean printOnBothSides) {
        this.printOnBothSides = printOnBothSides;
    }

    public Boolean getPrintOnBothSides() {
        return printOnBothSides;
    }


}