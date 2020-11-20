package org.financer.shared.domain.model.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;

@Data
@Accessors(chain = true)
@Schema(description = "Initial Admin Configuration")
public class InitAdminConfigurationDTO extends UpdateAdminConfigurationDTO {

    @Schema(description = "Admin User that will be created")
    private RegisterUserDTO adminUser;

}
