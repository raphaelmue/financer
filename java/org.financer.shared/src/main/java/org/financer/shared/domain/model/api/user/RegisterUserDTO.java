package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class RegisterUserDTO implements DataTransferObject {

    @NotNull
    private Email email;

    @NotNull
    private Name name;

    @NotNull
    private HashedPassword password;

    private BirthDate birthDate;

    private Gender gender;

    public Email getEmail() {
        return email;
    }

    public RegisterUserDTO setEmail(Email email) {
        this.email = email;
        return this;
    }

    public Name getName() {
        return name;
    }

    public RegisterUserDTO setName(Name name) {
        this.name = name;
        return this;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public RegisterUserDTO setPassword(HashedPassword password) {
        this.password = password;
        return this;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public RegisterUserDTO setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public RegisterUserDTO setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
