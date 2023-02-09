package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.*;

import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "RegisterUser", description = "Schema to register a new user")
public class RegisterUserDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Email address of the user", required = true, example = "test@gmail.com")
    private Email email;

    @NotNull
    @Schema(description = "Name of the user", required = true)
    private Name name;

    @NotNull
    @Schema(description = "Hashed password of the user", required = true)
    private HashedPassword password;

    @Schema(description = "Birth date of the user")
    private BirthDate birthDate;

    @Schema(description = "Gender of the user")
    private Gender gender;

}
