package org.financer.server.domain.model.user;


import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.shared.domain.model.value.objects.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity implements DataEntity {
    private static final long serialVersionUID = 8551108621522985674L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private HashedPassword password;

    @Embedded
    private Name name;

    @Embedded
    private BirthDate birthDate;

    @Embedded
    private Gender gender;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CategoryEntity> categories = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SettingEntity> settings = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<TokenEntity> tokens = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private VerificationTokenEntity verificationToken;

    /**
     * Indicates whether the email of the user is verified.
     *
     * @return true if user is verified, false otherwise
     */
    public boolean isVerified() {
        return this.verificationToken != null && this.verificationToken.getVerifyingDate() != null &&
                !this.verificationToken.getVerifyingDate().isBefore(LocalDate.now());
    }

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public UserEntity setId(long id) {
        this.id = id;
        return this;
    }

    public Email getEmail() {
        return email;
    }

    public UserEntity setEmail(Email email) {
        this.email = email;
        return this;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public UserEntity setPassword(HashedPassword password) {
        this.password = password;
        return this;
    }

    public Name getName() {
        return name;
    }

    public UserEntity setName(Name name) {
        this.name = name;
        return this;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public UserEntity setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public UserEntity setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public UserEntity setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
        return this;
    }

    public Set<SettingEntity> getSettings() {
        return settings;
    }

    public UserEntity setSettings(Set<SettingEntity> databaseSettings) {
        this.settings = databaseSettings;
        return this;
    }

    public Set<TokenEntity> getTokens() {
        return tokens;
    }

    public UserEntity setTokens(Set<TokenEntity> tokens) {
        this.tokens = tokens;
        return this;
    }

    public VerificationTokenEntity getVerificationToken() {
        return verificationToken;
    }

    public UserEntity setVerificationToken(VerificationTokenEntity verificationToken) {
        this.verificationToken = verificationToken;
        return this;
    }
}
