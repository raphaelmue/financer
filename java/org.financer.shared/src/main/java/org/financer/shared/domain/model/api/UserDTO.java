package org.financer.shared.domain.model.api;

import com.google.gson.annotations.SerializedName;
import org.financer.shared.domain.model.value.objects.Gender;

import java.time.LocalDate;
import java.util.List;

// @Validated
public class UserDTO {

    @SerializedName("id")
    // @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @SerializedName("email")
    // @ApiModelProperty(value = "Email address", required = true, example = "test@email.com")
    private String email;

    @SerializedName("name")
    // @ApiModelProperty(value = "First name", required = true, example = "John")
    private String name;

    @SerializedName("surname")
    // @ApiModelProperty(value = "Surname", required = true, example = "Doe")
    private String surname;

    @SerializedName("birthDate")
    // @ApiModelProperty(value = "Birth Date", example = "02.03.1985")
    private LocalDate birthDate;

    @SerializedName("gender")
    // @ApiModelProperty(value = "Birth Date", example = "male", allowableValues = "male, female, notSpecified")
    private Gender.Values gender;

    @SerializedName("categories")
    // @ApiModelProperty(value = "Categories")
    private List<CategoryDTO> categories;

    @SerializedName("settings")
    // @ApiModelProperty(value = "Settings")
    private List<SettingsDTO> databaseSettings;

    @SerializedName("token")
    // @ApiModelProperty(value = "Active Token")
    private TokenDTO token;

    @SerializedName("verified")
    // @ApiModelProperty(value = "Indicates whether user is verified", required = true, example = "false")
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

    public Gender.Values getGender() {
        return gender;
    }

    public UserDTO setGender(Gender.Values gender) {
        this.gender = gender;
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
}
