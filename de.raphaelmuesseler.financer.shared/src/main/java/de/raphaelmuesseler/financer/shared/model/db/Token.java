package de.raphaelmuesseler.financer.shared.model.db;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Token implements DatabaseObject, Serializable {
    private static final long serialVersionUID = -769078637730799395L;

    private int id;
    private String token, system;

    @SerializedName("ip_address")
    private String ipAddress;
    @SerializedName("expire_date")
    private String expireDate;
    @SerializedName("is_mobile")
    private boolean isMobile;

    public Token(int id, String token, String ipAddress, String system, LocalDate expireDate, boolean isMobile) {
        this.id = id;
        this.token = token;
        this.ipAddress = ipAddress;
        this.system = system;
        this.expireDate = expireDate.toString();
        this.isMobile = isMobile;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSystem() {
        return system;
    }

    public LocalDate getExpireDate() {
        return LocalDate.parse(expireDate);
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate.toString();
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }

    @Override
    public String toString() {
        return this.getToken();
    }
}
