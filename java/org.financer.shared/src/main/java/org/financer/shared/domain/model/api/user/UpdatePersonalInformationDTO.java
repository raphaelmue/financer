package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.BirthDate;
import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.Name;

public class UpdatePersonalInformationDTO implements DataTransferObject {

    private Name name;
    private BirthDate birthDate;
    private Gender.Values gender;


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

    public Gender.Values getGender() {
        return gender;
    }

    public UpdatePersonalInformationDTO setGender(Gender.Values gender) {
        this.gender = gender;
        return this;
    }
}
