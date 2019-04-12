package de.raphaelmuesseler.financer.shared.model.user;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;

import java.io.Serializable;
import java.time.LocalDate;

public class Token extends DatabaseToken implements Serializable {
    private static final long serialVersionUID = -769078637730799395L;

    public Token(int id, String token, String ipAddress, String system, LocalDate expireDate, boolean isMobile) {
        this.setId(id);
        this.setToken(token);
        this.setIpAddress(ipAddress);
        this.setSystem(system);
        this.setExpireDate(expireDate);
        this.setIsMobile(isMobile);
    }

    public boolean isValid() {
        return LocalDate.now().isBefore(this.getExpireDate());
    }

    @Override
    public String toString() {
        return this.getToken();
    }
}
