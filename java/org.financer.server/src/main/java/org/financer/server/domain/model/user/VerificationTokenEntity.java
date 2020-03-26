package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "verification_tokens")
public class VerificationTokenEntity implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;

    @Embedded
    private TokenString token;

    @Embedded
    private ExpireDate expireDate;

    @Column(name = "verifying_date")
    private LocalDate verifyingDate;

    @Override
    public long getId() {
        return id;
    }

    public VerificationTokenEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public VerificationTokenEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public TokenString getToken() {
        return token;
    }

    public VerificationTokenEntity setToken(TokenString token) {
        this.token = token;
        return this;
    }

    public ExpireDate getExpireDate() {
        return expireDate;
    }

    public VerificationTokenEntity setExpireDate(ExpireDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public LocalDate getVerifyingDate() {
        return verifyingDate;
    }

    public void setVerifyingDate(LocalDate verifyingDate) {
        this.verifyingDate = verifyingDate;
    }
}
