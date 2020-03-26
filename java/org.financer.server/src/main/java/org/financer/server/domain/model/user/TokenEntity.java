package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.OperatingSystem;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_tokens")
public class TokenEntity implements DataEntity {
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
    public TokenEntity clone() {
        try {
            return (TokenEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public TokenEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public TokenEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public TokenString getToken() {
        return token;
    }

    public TokenEntity setToken(TokenString token) {
        this.token = token;
        return this;
    }

    public ExpireDate getExpireDate() {
        return expireDate;
    }

    public TokenEntity setExpireDate(ExpireDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public TokenEntity setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public TokenEntity setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenEntity that = (TokenEntity) o;
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
