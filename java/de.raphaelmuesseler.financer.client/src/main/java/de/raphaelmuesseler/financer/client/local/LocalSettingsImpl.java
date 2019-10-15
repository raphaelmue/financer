package de.raphaelmuesseler.financer.client.local;

import java.util.HashMap;
import java.util.Map;

public class LocalSettingsImpl implements LocalSettings {
    private static final long serialVersionUID = -7328386947589105017L;

    private final Map<String, String> properties = new HashMap<>();

    @Override
    public String getValueByProperty(Property property) {
        return this.properties.getOrDefault(property.getName(), property.getDefaultValue());
    }

    @Override
    public void setValueByProperty(Property property, String value) {
        this.properties.put(property.getName(), value);
    }

    @Override
    public int getMaxNumberOfMonthsDisplayed() {
        return Integer.valueOf(this.getValueByProperty(Property.MAX_NUMBER_OF_MONTHS_DISPLAYED));
    }

    @Override
    public void setMaxNumberOfMonthsDisplayed(int maxNumberOfMonthsDisplayed) {
        this.setValueByProperty(Property.MAX_NUMBER_OF_MONTHS_DISPLAYED, Integer.toString(maxNumberOfMonthsDisplayed));
    }
}
