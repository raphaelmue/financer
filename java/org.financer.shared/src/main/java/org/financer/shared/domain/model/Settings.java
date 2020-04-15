package org.financer.shared.domain.model;

import org.financer.shared.domain.model.value.objects.SettingPair;

/**
 * Represents users settings.
 *
 * @author Raphael Müßeler
 */
public interface Settings {

    /**
     * Puts a new setting property or updates the value if the property is already set.
     *
     * @param property property to set
     * @param value    value to set
     * @param <T>      type of the value
     */
    <T> void putOrUpdateSettingProperty(SettingPair.Property property, T value);

    /**
     * Returns either the value if exists or the default value.
     *
     * @param property property to get value
     * @param <T>      type of the value
     * @return value object
     */
    @SuppressWarnings("unchecked")
    default <T> T getValueOrDefault(SettingPair.Property property) {
        Object value = getValue(property);
        if (value == null) {
            return (T) property.getDefaultValue();
        }
        return (T) value;
    }

    /**
     * Returns the value or null if not exist.
     *
     * @param property property to get value
     * @param <T>      type of the value
     * @return value object or null
     */
    <T> T getValue(SettingPair.Property property);

}
