package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;

public interface DataEntity extends Serializable {

    int getId();

    /**
     * Creates a new instance of the super class, that is defined as a hibernate entity.
     *
     * @return DAO
     */
    default DataEntity toEntity() {
        throw new IllegalArgumentException("No implementation defined");
    }
}
