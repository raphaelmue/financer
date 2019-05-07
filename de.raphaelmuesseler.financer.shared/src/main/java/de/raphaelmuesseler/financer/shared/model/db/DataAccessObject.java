package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;

public interface DataAccessObject extends Serializable {

    int getId();

    /**
     * Creates a new instance of the super class, that is defined as a hibernate entity.
     *
     * @return DAO
     */
    default DataAccessObject toDatabaseAccessObject() {
        throw new IllegalArgumentException("No implementation defined");
    }
}
