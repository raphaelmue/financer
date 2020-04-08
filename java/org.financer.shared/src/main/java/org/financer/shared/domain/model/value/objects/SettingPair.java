package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Embeddable
@Immutable
public class SettingPair implements Serializable {
    private static final long serialVersionUID = -9194098816495257592L;

    public enum Property {
        LANGUAGE("language", true, "en"),
        CURRENCY("currency", true, "USD"),
        SHOW_CURRENCY_SIGN("showCurrencySign", true, "false"),
        CHANGE_AMOUNT_SIGN_AUTOMATICALLY("changeAmountSignAutomatically", true, "false"),
        MAX_NUMBER_OF_MONTHS_DISPLAYED("maxNumberOfMonthsDisplayed", false, "6"),
        THEME("theme", true, "");

        private final String name;
        private final String defaultValue;
        private final boolean isUserProperty;

        Property(String name, boolean isUserProperty, String defaultValue) {
            this.name = name;
            this.isUserProperty = isUserProperty;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public boolean isUserProperty() {
            return isUserProperty;
        }

        public static Property getPropertyByName(String name) {
            for (Property property : values()) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
            throw new IllegalArgumentException("No such property with name: " + name);
        }

        public static Set<Property> getUserProperties() {
            Set<Property> result = new HashSet<>(Arrays.asList(values()));
            result.removeIf(Property::isUserProperty);
            return result;
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "property", nullable = false)
    private Property property;

    @Column(name = "value", nullable = false)
    private String value;

    public SettingPair() {
    }

    public SettingPair(Property property, String value) {
        this.property = property;
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingPair that = (SettingPair) o;
        return property == that.property &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, value);
    }

    @Override
    public String toString() {
        return "SettingPair [" +
                "property=" + property +
                ", value='" + value + '\'' +
                ']';
    }
}
