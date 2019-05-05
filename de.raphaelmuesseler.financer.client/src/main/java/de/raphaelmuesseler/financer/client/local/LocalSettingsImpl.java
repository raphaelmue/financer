package de.raphaelmuesseler.financer.client.local;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalSettingsImpl implements Serializable, LocalSettings {
    private static final long serialVersionUID = -7328386947589105017L;

    private final Map<String, String> properties = new HashMap<>();

    @Override
    public String getValueByProperty(Property property) {
        return this.properties.get(property.getName());
    }

    @Override
    public void setValueByProperty(Property property, String value) {
        this.properties.put(property.getName(), value);
    }
}
