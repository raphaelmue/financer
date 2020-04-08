package org.financer.shared.domain.model.value.objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
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
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "system")
    private Values operatingSystem;

    public OperatingSystem() {
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
