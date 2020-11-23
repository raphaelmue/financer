package org.financer.server.domain.model.user;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User implements DataEntity, UserProperty {
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

    public void putOrUpdateSettingProperty(SettingPair.Property property, String value) {
        if (this.getSettings().containsKey(property)) {
            this.getSettings().get(property).setValue(value);
        } else {
            this.getSettings().put(property, new Setting()
                    .setUser(this)
                    .setPair(new SettingPair(property, value)));
        }
    }
}
