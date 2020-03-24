package org.financer.shared.domain.model.value.objects;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Immutable
public final class IPAddress {

    @Column(name = "ip_address")
    private final String ipAddress;

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
