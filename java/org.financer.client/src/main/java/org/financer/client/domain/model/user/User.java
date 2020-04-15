package org.financer.client.domain.model.user;


import org.financer.client.domain.model.category.Category;
import org.financer.shared.domain.model.Settings;
import org.financer.shared.domain.model.value.objects.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User implements Serializable, Settings {
    private static final long serialVersionUID = 8551108621522985674L;

    private Long id;
    private Email email;
    private HashedPassword password;
    private Name name;
    private BirthDate birthDate;
    private Gender gender;
    private Set<Category> categories = new HashSet<>();
    private Map<SettingPair.Property, Setting> settings;
    private Set<Token> tokens = new HashSet<>();
    private Token activeToken;
    private boolean isVerified;


    @Override
    public <T> void putOrUpdateSettingProperty(SettingPair.Property property, T value) {
        if (this.getSettings().containsKey(property)) {
            this.getSettings().get(property).setValue(value.toString());
        } else {
            this.getSettings().put(property, new Setting()
                    .setUser(this)
                    .setPair(new SettingPair(property, value.toString())));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(SettingPair.Property property) {
        return (T) this.settings.get(property).getPair().getValueObject();
    }

    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public Email getEmail() {
        return email;
    }

    public User setEmail(Email email) {
        this.email = email;
        return this;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public User setPassword(HashedPassword password) {
        this.password = password;
        return this;
    }

    public Name getName() {
        return name;
    }

    public User setName(Name name) {
        this.name = name;
        return this;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public User setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public User setCategories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public Map<SettingPair.Property, Setting> getSettings() {
        return settings;
    }

    public User setSettings(Map<SettingPair.Property, Setting> settings) {
        this.settings = settings;
        return this;
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public User setTokens(Set<Token> tokens) {
        this.tokens = tokens;
        return this;
    }

    public Token getActiveToken() {
        return activeToken;
    }

    public User setActiveToken(Token activeToken) {
        this.activeToken = activeToken;
        return this;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public User setVerified(boolean verified) {
        isVerified = verified;
        return this;
    }
}
