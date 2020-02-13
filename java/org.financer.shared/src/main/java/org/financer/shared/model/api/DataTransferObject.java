package org.financer.shared.model.api;

import org.financer.shared.model.db.DataEntity;
import org.springframework.validation.annotation.Validated;
import com.google.gson.Gson;


@Validated
public interface DataTransferObject {

    /**
     * Parses an Data Transfer Object to a Data Entity. Information may be lost during this process because DTOs do not contain all information.
     *
     * @return Data Entity
     */
    DataEntity toEntity();

    /**
     * Serializes the DataTransferObject in order to pass it to the client.
     *
     * @return serialized DTO
     */
    default String toJson() {
        return new Gson().toJson(this);
    }

}
