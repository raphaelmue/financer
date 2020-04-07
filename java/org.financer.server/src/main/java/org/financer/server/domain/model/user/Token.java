package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.OperatingSystem;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tokens", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
public class Token implements DataEntity, UserProperty {
    private static final long serialVersionUID = 8834445127500149942L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;

    @Embedded
    private TokenString token;

    @Embedded
    private ExpireDate expireDate;

    @Embedded
    private IPAddress ipAddress;

    @Embedded
    private OperatingSystem operatingSystem;

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getUser().getId() == userId;
    }

    @Override
    public long getId() {
        return id;
    }

    public Token setId(long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public Token setUser(UserEntity user) {
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
