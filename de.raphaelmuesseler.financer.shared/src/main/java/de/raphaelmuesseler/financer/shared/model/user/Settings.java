package de.raphaelmuesseler.financer.shared.model.user;

import java.util.*;

public interface Settings {
    enum Property {
        LANGUAGE("language", true, "en"),
        CURRENCY("currency", true, "USD"),
        SHOW_CURRENCY_SIGN("showCurrencySign", true, "false"),
        CHANGE_AMOUNT_SIGN_AUTOMATICALLY("changeAmountSignAutomatically", true, "false"),
        MAX_NUMBER_OF_MONTHS_DISPLAYED("maxNumberOfMonthsDisplayed", false, "6"),
        THEME("theme", true, "");

        private final String name, defaultValue;
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

    void setValueByProperty(Property property, String value);

    String getValueByProperty(Property property);

}
