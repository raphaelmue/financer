package org.financer.shared.domain.model.value.objects;

import org.financer.shared.domain.model.Settings;

import java.util.Currency;
import java.util.Locale;

public abstract class MockData {

    protected Settings settings() {
        return new Settings() {
            @Override
            public <T> void putOrUpdateSettingProperty(SettingPair.Property property, T value) {
            }

            @Override
            public <T> T getValue(SettingPair.Property property) {
                if (property == SettingPair.Property.CURRENCY) return (T) Currency.getInstance("EUR");
                if (property == SettingPair.Property.LANGUAGE) return (T) Locale.GERMAN;
                return null;
            }
        };
    }

}
