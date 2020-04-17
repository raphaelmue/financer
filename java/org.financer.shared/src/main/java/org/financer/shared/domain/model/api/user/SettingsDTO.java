package org.financer.shared.domain.model.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Schema(name = "Settings", description = "Schema for users settings")
public class SettingsDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the setting", required = true, example = "1")
    private int id;

    @NotNull
    @JsonProperty("value")
    @Schema(description = "Value of the setting", required = true, example = "true")
    private String pairValue;

    public int getId() {
        return id;
    }

    public SettingsDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getPairValue() {
        return pairValue;
    }

    public SettingsDTO setPairValue(String pairValue) {
        this.pairValue = pairValue;
        return this;
    }
}
