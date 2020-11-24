package org.financer.server.application.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Message that is displayed when an exception is thrown.
 */
@Data
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

    public RestErrorMessage(HttpStatus httpStatus, String path, String message) {
        this(httpStatus, path, Collections.singletonList(message));
    }

    public RestErrorMessage(RestException exception, String path, String message) {
        this(exception.getHttpStatus(), path, Collections.singletonList(message));
    }
}

