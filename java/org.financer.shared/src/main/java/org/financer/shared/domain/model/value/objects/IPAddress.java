package org.financer.shared.domain.model.value.objects;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Immutable
public final class IPAddress implements Serializable {
    private static final long serialVersionUID = 827262547418532497L;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    public IPAddress() {
    }

    public IPAddress(String ipAddress) {
        if (!InetAddressValidator.getInstance().isValid(ipAddress)) {
            throw new IllegalArgumentException("IP Address ('" + ipAddress + "')' is not valid!");
        }
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
