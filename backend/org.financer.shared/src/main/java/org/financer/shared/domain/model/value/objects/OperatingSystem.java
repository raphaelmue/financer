package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.exceptions.EnumNotFoundException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;

@Data
@Embeddable
@Schema(description = "Value object for the operating system")
public class OperatingSystem implements Serializable {
    private static final long serialVersionUID = -3238265087964566580L;

    @Schema(name = "OperatingSystemEnum")
    public enum Values {
        WINDOWS("windows", false),
        LINUX("linux", false),
        MAC_OS("mac_os", false),
        ANDROID("android", true),
        I_OS("i_os", true);

        private final String name;
        private final boolean isMobile;

        Values(String name, boolean isMobile) {
            this.name = name;
            this.isMobile = isMobile;
        }

        public String getName() {
            return name;
        }

        public boolean getIsMobile() {
            return isMobile;
        }

        public static Values getOperatingSystemByName(String name) {
            for (Values operatingSystem : values()) {
                if (operatingSystem.getName().equals(name)) {
                    return operatingSystem;
                }
            }
            throw new EnumNotFoundException(Values.class, name);
        }
    }

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "system")
    @Schema(description = "Operating system", required = true, example = "MAC_OS", enumAsRef = true)
    private Values operatingSystem;

    public OperatingSystem() {
    }

    public OperatingSystem(String operatingSystem) {
        this.operatingSystem = Values.getOperatingSystemByName(operatingSystem);
    }

    public OperatingSystem(Values operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}
