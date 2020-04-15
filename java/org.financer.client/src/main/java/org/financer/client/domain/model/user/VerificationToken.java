package org.financer.client.domain.model.user;

import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.TokenString;

import java.time.LocalDate;
import java.util.Objects;

public class VerificationToken {

    private long id;
    private User user;
    private TokenString token;
    private ExpireDate expireDate;
    private LocalDate verifyingDate;

    public long getId() {
        return id;
    }

    public VerificationToken setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public VerificationToken setUser(User user) {
        this.user = user;
        return this;
    }

    public TokenString getToken() {
        return token;
    }

    public VerificationToken setToken(TokenString token) {
        this.token = token;
        return this;
    }

    public ExpireDate getExpireDate() {
        return expireDate;
    }

    public VerificationToken setExpireDate(ExpireDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public LocalDate getVerifyingDate() {
        return verifyingDate;
    }

    public void setVerifyingDate(LocalDate verifyingDate) {
        this.verifyingDate = verifyingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return id == that.id &&
                Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token);
    }
}
