package org.financer.server.application.api.error;

import org.financer.server.domain.model.DataEntity;
import org.springframework.http.HttpStatus;

/**
 * Thrown when an entity is not found.
 *
 * @author Raphael Müßeler
 */
public class NotFoundException extends RestException {

    private static final String MESSAGE = "No record of %s could be found with identifier %s.";
    private static final String MESSAGE_KEY = "exception.notFound";

    /**
     * Instantiates a not found exception
     *
     * @param entityClassName class name of which the entity is not found
     * @param identifier      identifier which is not found
     */
    public NotFoundException(Class<? extends DataEntity> entityClassName, Long identifier) {
        super(String.format(MESSAGE, entityClassName.getName(), identifier.toString()), HttpStatus.NOT_FOUND, MESSAGE_KEY, entityClassName.getName(), identifier.toString());
    }
}
