package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.financer.shared.model.user.Settings;
import org.springframework.validation.annotation.Validated;

@Validated
public class SettingsDTO {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("property")
    @ApiModelProperty(value = "Property", required = true)
    private Settings.Property property;

    @JsonProperty("value")
    @ApiModelProperty(value = "Value", required = true)
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
