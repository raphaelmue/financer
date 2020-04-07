package org.financer.server.domain.model.user;


import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
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
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKey(name = "pair.property")
    private Map<String, Setting> settings;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Token> tokens = new HashSet<>();

    private transient Token activeToken;

    @OneToOne(mappedBy = "user")
    private VerificationToken verificationToken;

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

    public Set<Category> getCategories() {
        return categories;
    }

    public UserEntity setCategories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public Map<String, Setting> getSettings() {
        return settings;
    }

    public UserEntity setSettings(Map<String, Setting> settings) {
        this.settings = settings;
        return this;
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public UserEntity setTokens(Set<Token> tokens) {
        this.tokens = tokens;
        return this;
    }

    /**
     * Returns the active token for the current session.
     *
     * @return
     */
    public Token getActiveToken() {
        return activeToken;
    }

    public UserEntity setActiveToken(Token activeToken) {
        this.activeToken = activeToken;
        return this;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public UserEntity setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
        return this;
    }
}
