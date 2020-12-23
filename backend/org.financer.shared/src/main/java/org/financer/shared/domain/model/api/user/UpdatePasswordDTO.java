package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.HashedPassword;

@Data
@Accessors(chain = true)
@Schema(name = "UpdatePassword", description = "Schema for updating password")
public class UpdatePasswordDTO implements DataTransferObject {

    @Schema(description = "Password of the current user", required = true)
    private String password;

    @Schema(description = "Updated password for user", required = true)
    private HashedPassword updatedPassword;

}
