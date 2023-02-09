package org.financer.server.domain.model.user;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.Settings;
import org.financer.shared.domain.model.value.objects.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User implements DataEntity, UserProperty, AmountProvider, Settings {
    private static final long serialVersionUID = 8551108621522985674L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKey(name = "pair.property")
    private Map<SettingPair.Property, Setting> settings = new EnumMap<>(SettingPair.Property.class);

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Token> tokens = new HashSet<>();

    @Transient
    private Token activeToken;

    @OneToOne(mappedBy = "user")
    private VerificationToken verificationToken;

    /**
     * Indicates whether the email of the user is verified.
     *
     * @return true if user is verified, false otherwise
     */
    public boolean isVerified() {
        return this.verificationToken != null && this.verificationToken.getVerifyingDate() != null &&
                !this.verificationToken.getVerifyingDate().isAfter(LocalDate.now()) &&
                !this.verificationToken.getExpireDate().getExpireDate().isBefore(this.verificationToken.getVerifyingDate());
    }

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.id == userId;
    }

    @Override
    public <T> void putOrUpdateSettingProperty(SettingPair.Property property, T value) {
        if (this.getSettings().containsKey(property)) {
            this.getSettings().get(property).setValue(value.toString());
        } else {
            this.getSettings().put(property, new Setting()
                    .setUser(this)
                    .setPair(new SettingPair(property, value)));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(SettingPair.Property property) {
        if (this.getSettings() != null && this.getSettings().get(property) != null) {
            return (T) this.getSettings().get(property).getPair().getValueObject();
        }
        return null;
    }

    @Override
    public Amount getTotalAmount() {
        Amount amount = new Amount();
        for (Category category : this.categories) {
            amount = amount.add(category.getTotalAmount());
        }
        return amount;
    }

    @Override
    public Amount getTotalAmount(ValueDate valueDate) {
        Amount amount = new Amount();
        for (Category category : this.categories) {
            if (category.isRoot()) {
                amount = amount.add(category.getTotalAmount(valueDate));
            }
        }
        return amount;
    }

    @Override
    public Amount getTotalAmount(TimeRange timeRange) {
        Amount amount = new Amount();
        for (Category category : this.categories) {
            amount = amount.add(category.getTotalAmount(timeRange));
        }
        return amount;
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean isRevenue() {
        return false;
    }

    @Override
    public void adjustAmountSign() {
        for (Category category : this.categories) {
            category.adjustAmountSign();
        }
    }
}
