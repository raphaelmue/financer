package de.raphaelmuesseler.financer.shared.model.db;

public interface DataAccessObject {

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
