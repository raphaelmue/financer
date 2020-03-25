package org.financer.shared.domain.model.value.objects;

import org.financer.util.RandomString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Immutable
public final class TokenString {

    private static final RandomString stringGenerator = new RandomString(64);

    @Column(name = "token", length = 64)
    private final String token;

    public TokenString(String token) {
        this.token = token;
    }

    public TokenString() {
        this.token = stringGenerator.nextString();
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
}
