package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.BirthDate;
import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.Name;


@Schema(name = "UpdatePersonalInformation", description = "Schema for updating users personal information")
public class UpdatePersonalInformationDTO implements DataTransferObject {

    @Schema(description = "Name of the user", required = true)
    private Name name;

    @Schema(description = "Birth date of the user", example = "2020-01-01")
    private BirthDate birthDate;

    @Schema(description = "Gender of the user", example = "FEMALE", enumAsRef = true)
    private Gender gender;


    public Name getName() {
        return name;
    }

    public UpdatePersonalInformationDTO setName(Name name) {
        this.name = name;
        return this;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public UpdatePersonalInformationDTO setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public UpdatePersonalInformationDTO setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
