package org.financer.shared.domain.model.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;

@Data
@Accessors(chain = true)
@Schema(description = "Update Admin Configuration Schema")
public class UpdateAdminConfigurationDTO implements DataTransferObject {

    @Schema(description = "Default language for server", example = "en", allowableValues = {"en", "de"})
    private String defaultLanguage;

    @Schema(description = "Default currency for server", example = "USD")
    private String defaultCurrency;

}
