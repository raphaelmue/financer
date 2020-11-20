package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;

@Data
@Accessors(chain = true)
@Schema(name = "Role", description = "Schema for a user role")
public class RoleDTO implements DataTransferObject {

    @Schema(description = "Id of the role", example = "1")
    private long id;

    @Schema(description = "Role name", example = "USER")
    private String name;

}
