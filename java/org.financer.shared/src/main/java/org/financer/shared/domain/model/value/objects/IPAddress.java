package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
@Schema(description = "Value object for IP address")
public final class IPAddress implements Serializable {
    private static final long serialVersionUID = 827262547418532497L;

    @Column(name = "ip_address", nullable = false)
    @Schema(description = "IP address", required = true, example = "192.168.0.1")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress1 = (IPAddress) o;
        return Objects.equals(ipAddress, ipAddress1.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress);
    }

    @Override
    public String toString() {
        return "IPAddress [" +
                "ipAddress='" + ipAddress + '\'' +
                ']';
    }


}
