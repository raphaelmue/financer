package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.value.objects.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@Schema(name = "User", description = "Schema for a user")
public class UserDTO extends RepresentationModel<UserDTO> {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the user", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Email address of the user", required = true, example = "test@gmail.com")
    private Email email;

    @NotNull
    @Schema(description = "Name of the user", required = true)
    private Name name;

    @Schema(description = "Birth date of the user", required = true, example = "2020-01-01")
    private BirthDate birthDate;

    @Schema(description = "Gender of the user", required = true, example = "FEMALE")
    private Gender gender;

    @NotNull
    @Schema(description = "Active token of the user that is used to authorize API calls for this client",
            required = true)
    private TokenDTO activeToken;

    @Schema(description = "List of all tokens that are assigned to the user")
    private List<TokenDTO> tokens;

    private List<RoleDTO> roles;

    @NotNull
    private boolean verified;

    private Map<SettingPair.Property, SettingsDTO> settings;

}
