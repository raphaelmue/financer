package org.financer.shared.domain.model.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SettingsDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    @JsonProperty("value")
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
