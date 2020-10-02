package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.*;

import javax.validation.constraints.NotNull;

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

    @Schema(description = "Birth date of the user", example = "2020-01-01")
    private BirthDate birthDate;

    @Schema(description = "Gender of the user", example = "FEMALE")
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
