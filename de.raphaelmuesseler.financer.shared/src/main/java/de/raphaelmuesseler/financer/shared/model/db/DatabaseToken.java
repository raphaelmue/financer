package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;

public class DatabaseToken {
    private int id;
    private DatabaseUser user;
    private String token;
    private LocalDate expireDate;
    private String ipAddress;
    private String system;
    private boolean isMobile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DatabaseUser getUser() {
        return user;
    }

    public void setUser(DatabaseUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public boolean getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean mobile) {
        isMobile = mobile;
    }
}
