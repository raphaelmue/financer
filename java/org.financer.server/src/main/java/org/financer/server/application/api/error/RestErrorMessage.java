package org.financer.server.application.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Message that is displayed when an exception is thrown.
 */
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

    public RestErrorMessage(HttpStatus httpStatus, String path, List<String> messages) {
        this.statusCode = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = path;
        this.messages = messages;
    }

    public RestErrorMessage(RestException exception, String path, String message) {
        this(exception.getHttpStatus(), path, Collections.singletonList(message));
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public RestErrorMessage setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}

