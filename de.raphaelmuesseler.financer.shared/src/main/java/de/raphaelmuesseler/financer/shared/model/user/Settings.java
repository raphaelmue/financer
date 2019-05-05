package de.raphaelmuesseler.financer.shared.model.user;

public interface Settings {
    enum Property {
        LANGUAGE("language", "en"),
        CURRENCY("currency", "USD"),
        SHOW_CURRENCY_SIGN("showCurrencySign", "false"),
        THEME("theme", "");

        private final String name, defaultValue;

        Property(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public static Property getPropertyByName(String name) {
            for (Property property : values()) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
            throw new IllegalArgumentException("No such property with name: " + name);
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }

    void setValueByProperty(Property property, String value);

    String getValueByProperty(Property property);

}
