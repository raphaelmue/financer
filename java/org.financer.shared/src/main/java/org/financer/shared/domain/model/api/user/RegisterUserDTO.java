package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class RegisterUserDTO implements DataTransferObject {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    @Size(min = 1, max = 64)
    private String surname;

    @NotNull
    @Size(min = 4, max = 64)
    private String password;

    private LocalDate birthDate;

    private Gender.Values gender;

    public String getEmail() {
        return email;
    }

    public RegisterUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public RegisterUserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public RegisterUserDTO setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUserDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public RegisterUserDTO setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender.Values getGender() {
        return gender;
    }

    public RegisterUserDTO setGender(Gender.Values gender) {
        this.gender = gender;
        return this;
    }
}
