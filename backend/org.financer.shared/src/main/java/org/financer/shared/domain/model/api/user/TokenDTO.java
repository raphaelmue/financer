package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.OperatingSystem;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "Token", description = "Schema of a token. A token is used to authorize the user when calling the API.")
public class TokenDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the token", required = true, example = "1")
    private int id;

    @Schema(description = "Token string of the token", required = true)
    private TokenString token;

    @Schema(description = "IP address of the client that uses the token", required = true, example = "192.168.0.1")
    private IPAddress ipAddress;

    @Schema(description = "Expire date of the token", required = true, example = "2020-01-01")
    private ExpireDate expireDate;

    @Schema(description = "Operating System of the token", required = true, example = "2020-01-01")
    private OperatingSystem operatingSystem;
}
