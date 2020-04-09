package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(max = 64)
    private String name;

    @NotNull
    @Size(max = 64)
    private String surname;

    private LocalDate birthDate;

    private Gender.Values gender;

    @NotNull
    private TokenDTO activeToken;

    private List<TokenDTO> tokens;

    @NotNull
    private boolean verified;

    private Map<SettingPair.Property, SettingsDTO> settings;

    public int getId() {
        return id;
    }

    public UserDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public UserDTO setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public UserDTO setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender.Values getGender() {
        return gender;
    }

    public UserDTO setGender(Gender.Values gender) {
        this.gender = gender;
        return this;
    }

    public TokenDTO getActiveToken() {
        return activeToken;
    }

    public UserDTO setActiveToken(TokenDTO activeToken) {
        this.activeToken = activeToken;
        return this;
    }

    public List<TokenDTO> getTokens() {
        return tokens;
    }

    public UserDTO setTokens(List<TokenDTO> tokens) {
        this.tokens = tokens;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public UserDTO setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    public Map<SettingPair.Property, SettingsDTO> getSettings() {
        return settings;
    }

    public UserDTO setSettings(Map<SettingPair.Property, SettingsDTO> settings) {
        this.settings = settings;
        return this;
    }
}
