package org.financer.client.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.financer.shared.domain.model.api.DataTransferObject;

import java.time.LocalDateTime;
import java.util.List;

public class RestErrorMessage implements DataTransferObject {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @JsonProperty("path")
    private String path;

    @JsonProperty("status")
    private int statusCode;

    @JsonProperty("error")
    private String error;

    @JsonProperty("messages")
    private List<String> messages;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public RestErrorMessage setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RestErrorMessage setPath(String path) {
        this.path = path;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public RestErrorMessage setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getError() {
        return error;
    }

    public RestErrorMessage setError(String error) {
        this.error = error;
        return this;
    }

    public List<String> getMessages() {
        return messages;
    }

    public RestErrorMessage setMessages(List<String> messages) {
        this.messages = messages;
        return this;
    }
}
