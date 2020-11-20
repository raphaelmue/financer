package org.financer.shared.domain.model.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Admin Configuration schema")
public class AdminConfigurationDTO {

    @Schema(description = "Default language for server", example = "en")
    private String defaultLanguage;

    @Schema(description = "Default currency for server", example = "USD")
    private String defaultCurrency;
}
