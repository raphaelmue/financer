package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.util.security.Hash;
import org.financer.util.security.RandomString;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for hashed password")
public final class HashedPassword implements Serializable, DataTransferObject {
    private static final long serialVersionUID = 158937550736458704L;

    private static final RandomString stringGenerator = new RandomString(32);

    @EqualsAndHashCode.Include
    @Column(name = "password", length = 64, nullable = false)
    @Schema(description = "Hashed password (SHA-256)", required = true, example = "ad6faf806c28538854bd49b5aee8476542eee997f9bf3f5dd9a1a4869f4fd0fc")
    private String hashedPassword;

    @EqualsAndHashCode.Include
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
}
