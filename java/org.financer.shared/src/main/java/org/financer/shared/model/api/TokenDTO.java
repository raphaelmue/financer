package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class TokenDTO {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("token")
    @ApiModelProperty(value = "Token", required = true, example = "4i8va0ß3jvaih4oa9vjio5nöas0vivj4oa")
    private String token;

    @JsonProperty("ipAddress")
    @ApiModelProperty(value = "IP Address", required = true, example = "192.168.0.1")
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
