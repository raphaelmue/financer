package org.financer.shared.domain.model.api;

import com.google.gson.annotations.SerializedName;
import org.financer.shared.model.user.Settings;

// @Validated
public class SettingsDTO {

    @SerializedName("id")
    // @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @SerializedName("property")
    // @ApiModelProperty(value = "Property", required = true)
    private Settings.Property property;

    @SerializedName("value")
    // @ApiModelProperty(value = "Value", required = true)
    private String value;

    public int getId() {
        return id;
    }

    public SettingsDTO setId(int id) {
        this.id = id;
        return this;
    }

    public Settings.Property getProperty() {
        return property;
    }

    public SettingsDTO setProperty(Settings.Property property) {
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
