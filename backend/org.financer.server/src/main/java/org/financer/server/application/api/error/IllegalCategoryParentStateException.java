package org.financer.server.application.api.error;

import org.financer.server.domain.model.category.Category;
import org.springframework.http.HttpStatus;

public class IllegalCategoryParentStateException extends RestException {

    private static final String MESSAGE = "";
    private static final String MESSAGE_KEY = "exception.illegalCategoryParentStateException";

    public IllegalCategoryParentStateException(Category category, Category parent) {
        super(MESSAGE, HttpStatus.BAD_REQUEST, MESSAGE_KEY, category.toString(), parent.toString());
    }

}
