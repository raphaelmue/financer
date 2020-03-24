package org.financer.shared.domain.model.value.objects;

import org.financer.util.RandomString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Immutable
public class TokenString {

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
}
