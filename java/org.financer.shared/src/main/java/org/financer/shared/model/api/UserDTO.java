package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.financer.shared.model.db.DataEntity;
import org.financer.shared.model.user.User;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public class UserDTO implements DataTransferObject {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("email")
    @ApiModelProperty(value = "Email address", required = true, example = "test@email.com")
    private String email;

    @JsonProperty("name")
    @ApiModelProperty(value = "First name", required = true, example = "John")
    private String name;

    @JsonProperty("surname")
    @ApiModelProperty(value = "Surname", required = true, example = "Doe")
    private String surname;

    @JsonProperty("birthDate")
    @ApiModelProperty(value = "Birth Date", example = "02.03.1985")
    private LocalDate birthDate;

    @JsonProperty("gender")
    @ApiModelProperty(value = "Birth Date", example = "male", allowableValues = "male, female, notSpecified")
    private User.Gender genderName;

    @JsonProperty("categories")
    @ApiModelProperty(value = "Categories")
    private List<CategoryDTO> categories;

    @JsonProperty("settings")
    @ApiModelProperty(value = "Settings")
    private List<SettingsDTO> databaseSettings;

    @JsonProperty("token")
    @ApiModelProperty(value = "Active Token")
    private TokenDTO token;

    @JsonProperty("verified")
    @ApiModelProperty(value = "Indicates whether user is verified", required = true, example = "false")
    private boolean verified;

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

    public User.Gender getGenderName() {
        return genderName;
    }

    public UserDTO setGenderName(User.Gender genderName) {
        this.genderName = genderName;
        return this;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public UserDTO setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
        return this;
    }

    public List<SettingsDTO> getDatabaseSettings() {
        return databaseSettings;
    }

    public UserDTO setDatabaseSettings(List<SettingsDTO> databaseSettings) {
        this.databaseSettings = databaseSettings;
        return this;
    }

    public TokenDTO getToken() {
        return token;
    }

    public UserDTO setToken(TokenDTO token) {
        this.token = token;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public UserDTO setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    @Override
    public DataEntity toEntity() {
        return null;
    }
}
