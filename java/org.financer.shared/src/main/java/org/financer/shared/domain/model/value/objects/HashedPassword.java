package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.util.Hash;
import org.financer.util.RandomString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
@Schema(description = "Value object for hashed password")
public final class HashedPassword implements Serializable {
    private static final long serialVersionUID = 158937550736458704L;

    private static final RandomString stringGenerator = new RandomString(32);

    @Column(name = "password", length = 64, nullable = false)
    @Schema(description = "Hashed password (SHA-256)", required = true, example = "ad6faf806c28538854bd49b5aee8476542eee997f9bf3f5dd9a1a4869f4fd0fc")
    private String hashedPassword;

    @Column(name = "salt", length = 32, nullable = false)
    @Schema(description = "Salt that is used to hash the password", required = true, example = "g8jjEVszgWgxo9jsKtlRxU6ZCgF8kbRX")
    private String salt;

    public HashedPassword() {
    }

    public HashedPassword(String plainPassword) {
        this.salt = stringGenerator.nextString();
        this.hashedPassword = Hash.create(plainPassword, this.salt);
    }

    public HashedPassword(String hashedPassword, String salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashedPassword that = (HashedPassword) o;
        return Objects.equals(hashedPassword, that.hashedPassword) &&
                Objects.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedPassword, salt);
    }

    @Override
    public String toString() {
        return "HashedPassword [" +
                "hashedPassword='" + hashedPassword + '\'' +
                ", salt='" + salt + '\'' +
                ']';
    }


}
