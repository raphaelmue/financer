package org.financer.shared.model.user;

import org.financer.shared.model.db.DataEntity;
import org.financer.shared.model.db.VerificationTokenEntity;

import java.time.LocalDate;

public class VerificationToken extends VerificationTokenEntity {

    public VerificationToken(int id, User user, String token, LocalDate expireDate) {
        this.setId(id);
        this.setUser(user);
        this.setToken(token);
        this.setExpireDate(expireDate);
    }

    @Override
    public DataEntity toEntity() {
        VerificationTokenEntity verificationTokenEntity = new VerificationTokenEntity();
        verificationTokenEntity.setId(this.getId());
        verificationTokenEntity.setUser(this.getUser());
        verificationTokenEntity.setToken(this.getToken());
        verificationTokenEntity.setExpireDate(this.getExpireDate());
        return verificationTokenEntity;
    }
}
