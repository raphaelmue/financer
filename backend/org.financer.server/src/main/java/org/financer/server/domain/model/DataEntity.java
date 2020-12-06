package org.financer.server.domain.model;

import java.io.Serializable;

/**
 * Marker interface for data entities that represent the persistent model.
 */
public interface DataEntity extends Serializable {

    /**
     * Returns the identifier of this data entity.
     *
     * @return unique identifier
     */
    Long getId();

}
