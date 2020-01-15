package de.raphaelmuesseler.financer.shared.model.user;

import de.raphaelmuesseler.financer.shared.model.db.TokenEntity;

import java.io.Serializable;
import java.time.LocalDate;

public class Token extends TokenEntity implements Serializable {
    private static final long serialVersionUID = -769078637730799395L;

    public Token(TokenEntity databaseToken) {
        this(databaseToken.getId(),
                databaseToken.getToken(),
                databaseToken.getIpAddress(),
                databaseToken.getOperatingSystem(),
                databaseToken.getExpireDate(),
                databaseToken.getIsMobile());
        if (databaseToken.getUser() != null) {
            this.setUser(new User(databaseToken.getUser()));
        }
    }

    public Token(int id, String token, String ipAddress, String system, LocalDate expireDate, boolean isMobile) {
        this.setId(id);
        this.setToken(token);
        this.setIpAddress(ipAddress);
        this.setOperatingSystem(system);
        this.setExpireDate(expireDate);
        this.setIsMobile(isMobile);
    }

    public boolean isValid() {
        return LocalDate.now().isBefore(this.getExpireDate());
    }

    @Override
    public User getUser() {
        return (User) super.getUser();
    }

    @Override
    public String toString() {
        return this.getToken();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Token && ((Token) obj).getToken().equals(this.getToken()));
    }

    @Override
    public int hashCode() {
        return this.getId() * this.getToken().hashCode();
    }
}
