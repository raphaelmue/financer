package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for IP address")
public final class IPAddress implements Serializable {
    private static final long serialVersionUID = 827262547418532497L;

    @EqualsAndHashCode.Include
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
}
