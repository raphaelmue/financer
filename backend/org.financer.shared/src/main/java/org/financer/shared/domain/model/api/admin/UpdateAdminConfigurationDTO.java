package org.financer.shared.domain.model.api.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateAdminConfiguration", description = "Update Admin Configuration Schema")
public class UpdateAdminConfigurationDTO extends AdminConfigurationDTO {
}
