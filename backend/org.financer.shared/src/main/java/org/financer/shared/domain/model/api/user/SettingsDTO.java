package org.financer.shared.domain.model.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
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

}
