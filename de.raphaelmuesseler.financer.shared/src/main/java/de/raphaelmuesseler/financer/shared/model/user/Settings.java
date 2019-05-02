package de.raphaelmuesseler.financer.shared.model.user;

public interface Settings {
    enum Property {
        CURRENCY("currency"),
        SHOW_CURRENCY_SIGN("showCurrencySign"),
        THEME("theme");

        private final String name;

        Property(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
