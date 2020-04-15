package org.financer.shared.domain.model.value.objects;

import org.financer.shared.exceptions.EnumNotFoundException;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@Immutable
public class SettingPair implements Serializable {
    private static final long serialVersionUID = -9194098816495257592L;

    public interface PropertyConverter<T> {
        T toObject(String value);
    }

    public enum Property {
        LANGUAGE("language", Locale.ENGLISH, Locale.class, Locale::forLanguageTag),
        CURRENCY("currency", Currency.getInstance("USD"), Currency.class, Currency::getInstance),
        SHOW_CURRENCY_SIGN("showCurrencySign", false, Boolean.class, Boolean::getBoolean),
        CHANGE_AMOUNT_SIGN_AUTOMATICALLY("changeAmountSignAutomatically", false, Boolean.class, Boolean::getBoolean),
        MAX_NUMBER_OF_MONTHS_DISPLAYED("maxNumberOfMonthsDisplayed", 6, Integer.class, Integer::getInteger),
        THEME("theme", "", String.class, value -> value);

        private final String name;
        private final Object defaultValue;
        private final Class<?> type;
        private final PropertyConverter<?> propertyConverter;

        Property(String name, Object defaultValue, Class<?> type, PropertyConverter<?> propertyConverter) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
            this.propertyConverter = propertyConverter;
        }

        public String getName() {
            return name;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public Class<?> getType() {
            return type;
        }

        public PropertyConverter<?> getPropertyConverter() {
            return propertyConverter;
        }

        public static Property getPropertyByName(String name) {
            for (Property property : values()) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
            throw new EnumNotFoundException(Property.class, name);
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

    public SettingPair(String property, String value) {
        this(Property.getPropertyByName(property), value);
    }

    public SettingPair(Property property, Object value) {
        this.property = property;
        this.value = value.toString();
    }

    public SettingPair(Property property, String value) {
        this.property = property;
        this.value = value;
    }

    public Object getValueObject() {
        return this.property.getPropertyConverter().toObject(this.value);
    }

    public Property getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public SettingPair setValue(String value) {
        return new SettingPair(this.property, value);
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
