package org.financer.client.domain.model.user;

import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.OperatingSystem;
import org.financer.shared.domain.model.value.objects.TokenString;

import java.util.Objects;

public class Token {
    private static final long serialVersionUID = 8834445127500149942L;

    private long id;
    private User user;
    private TokenString token;
    private ExpireDate expireDate;
    private IPAddress ipAddress;
    private OperatingSystem operatingSystem;

    /*
     * Getter and Setter
     */

    public long getId() {
        return id;
    }

    public Token setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Token setUser(User user) {
        this.user = user;
        return this;
    }

    public TokenString getToken() {
        return token;
    }

    public Token setToken(TokenString token) {
        this.token = token;
        return this;
    }

    public ExpireDate getExpireDate() {
        return expireDate;
    }

    public Token setExpireDate(ExpireDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public Token setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public Token setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token that = (Token) o;
        if (this.id >= 0) {
            return id == that.id;
        } else {
            return token.equals(that.token);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
