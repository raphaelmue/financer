package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Email;
import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.Name;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private Email email;

    @NotNull
    private Name name;

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

    public Email getEmail() {
        return email;
    }

    public UserDTO setEmail(Email email) {
        this.email = email;
        return this;
    }

    public Name getName() {
        return name;
    }

    public UserDTO setName(Name name) {
        this.name = name;
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
