package org.financer.shared.domain.model.api.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TokenDTO {

    @NotNull
    @Min(1)
    private int id;

    private String token;

    private String ipAddress;

    public int getId() {
        return id;
    }

    public TokenDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getToken() {
        return token;
    }

    public TokenDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public TokenDTO setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
}
