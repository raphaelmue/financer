package org.financer.shared.domain.model.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.shared.exceptions.JsonParseException;

public interface DataTransferObject {

    /**
     * Serializes the DataTransferObject in order to pass it to the client.
     *
     * @return serialized DTO
     */
    default String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e.getMessage(), e.getCause());
        }
    }

}
