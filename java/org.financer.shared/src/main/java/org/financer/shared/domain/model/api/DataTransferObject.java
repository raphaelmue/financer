package org.financer.shared.domain.model.api;

import com.google.gson.Gson;


// @Validated
public interface DataTransferObject {

    /**
     * Serializes the DataTransferObject in order to pass it to the client.
     *
     * @return serialized DTO
     */
    default String toJson() {
        return new Gson().toJson(this);
    }

}
