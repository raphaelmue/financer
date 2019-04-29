package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;
import java.time.LocalDate;

public class DatabaseToken implements Serializable, Cloneable {
    private final static long serialVersionUID = 8834445127500149942L;

    protected int id;
    protected DatabaseUser user;
    protected String token;
    protected LocalDate expireDate;
    protected String ipAddress;
    protected String system;
    protected boolean isMobile;

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
        this.user = (DatabaseUser) user;
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

    @Override
    public DatabaseToken clone() {
        try {
            return (DatabaseToken) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ClassCastException(e.getMessage());
        }
    }
}
