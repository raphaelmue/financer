package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SettingsDTO {

    @NotNull
    @Min(1)
    private int id;

    private SettingPair.Property property;

    private String value;

    public int getId() {
        return id;
    }

    public SettingsDTO setId(int id) {
        this.id = id;
        return this;
    }

    public SettingPair.Property getProperty() {
        return property;
    }

    public SettingsDTO setProperty(SettingPair.Property property) {
        this.property = property;
        return this;
    }

    public String getValue() {
        return value;
    }

    public SettingsDTO setValue(String value) {
        this.value = value;
        return this;
    }
}
