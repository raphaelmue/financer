package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.util.security.RandomString;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@Immutable
public final class TokenString implements Serializable {
    private static final long serialVersionUID = 5941856184087494870L;

    private static final RandomString stringGenerator = new RandomString(64);

    @EqualsAndHashCode.Include
    @Schema(description = "Token string", required = true, maxLength = 64, minLength = 64,
            example = "UPOPBEZR7c9GqpGs0IFrKdgtRvUuZMsI8ubfOK33cDX6ArmtFiqxnP6ayHQFHsbu")
    @Column(name = "token", length = 64, nullable = false)
    private final String token;

    /**
     * Instantiates the token by generating a token.
     */
    public TokenString() {
        this.token = stringGenerator.nextString();
    }

    public TokenString(String token) {
        this.token = token;
    }
}
