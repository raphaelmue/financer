package org.financer.shared.domain.model.value.objects;

import org.financer.util.Hash;
import org.financer.util.RandomString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Immutable
public final class HashedPassword {

    private static final RandomString stringGenerator = new RandomString(32);

    @Column(name = "password")
    private final String hashedPassword;

    @Column(name = "salt")
    private final String salt;

    public HashedPassword(String hashedPassword, String salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public HashedPassword(String plainPassword) {
        this.salt = stringGenerator.nextString();
        this.hashedPassword = Hash.create(plainPassword, this.salt);
    }

    public boolean isEqualTo(String plainPassword) {
        return this.hashedPassword.equals(Hash.create(plainPassword, this.salt));
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }
}
