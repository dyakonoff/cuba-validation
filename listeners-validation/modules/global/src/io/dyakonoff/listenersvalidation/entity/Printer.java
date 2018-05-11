package io.dyakonoff.listenersvalidation.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.NumberFormat;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.validation.constraints.Pattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("listenersvalidation_PrinterEntityListener")
@NamePattern("%s|name")
@Table(name = "LISTENERSVALIDATION_PRINTER")
@Entity(name = "listenersvalidation$Printer")
public class Printer extends StandardEntity {
    private static final long serialVersionUID = -3349851834559922352L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Lob
    @Column(name = "LOCATION")
    protected String location;

    @Pattern(message = "Incorrect IP address format", regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
    @NotNull
    @Column(name = "IP_ADDRESS", nullable = false, length = 40)
    protected String ipAddress;

    @NumberFormat(pattern = "#####")
    @Max(65535)
    @Min(1)
    @NotNull
    @Column(name = "PORT", nullable = false)
    protected Integer port;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }


}