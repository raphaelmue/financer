package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.util.RandomString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
public final class TokenString implements Serializable {
    private static final long serialVersionUID = 5941856184087494870L;

    private static final RandomString stringGenerator = new RandomString(64);

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

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenString that = (TokenString) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "TokenString [" +
                "token='" + token + '\'' +
                ']';
    }
}
