package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.exceptions.EnumNotFoundException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Schema(description = "Value object for the operating system")
public class OperatingSystem implements Serializable {
    private static final long serialVersionUID = -3238265087964566580L;

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

    public Values getOperatingSystem() {
        return operatingSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatingSystem that = (OperatingSystem) o;
        return operatingSystem == that.operatingSystem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operatingSystem);
    }

    @Override
    public String toString() {
        return "OperatingSystem [" +
                "operatingSystem=" + operatingSystem +
                ']';
    }
}