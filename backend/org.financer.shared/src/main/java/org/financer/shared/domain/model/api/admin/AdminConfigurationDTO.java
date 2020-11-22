package org.financer.shared.domain.model.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;

@Data
@Accessors(chain = true)
@Schema(name = "AdminConfiguration", description = "Admin Configuration schema")
public class AdminConfigurationDTO implements DataTransferObject {

    @Schema(description = "Default language for server", example = "en", allowableValues = {"en", "de"})
    private String defaultLanguage;

    @Schema(description = "Default currency for server", example = "USD", allowableValues = {"USD", "EUR"})
    private String defaultCurrency;
}
