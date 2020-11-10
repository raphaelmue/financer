package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.BirthDate;
import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.Name;

@Data
@Accessors(chain = true)
@Schema(name = "UpdatePersonalInformation", description = "Schema for updating users personal information")
public class UpdatePersonalInformationDTO implements DataTransferObject {

    @Schema(description = "Name of the user", required = true)
    private Name name;

    @Schema(description = "Birth date of the user", example = "2020-01-01")
    private BirthDate birthDate;

    @Schema(description = "Gender of the user", example = "FEMALE", enumAsRef = true)
    private Gender gender;

}
