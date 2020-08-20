package org.financer.server.application.api.error;

import org.financer.server.domain.model.category.Category;
import org.springframework.http.HttpStatus;

public class IllegalUpdateCategoryClassException extends RestException {

    private static final String FIXED = "fixed";
    private static final String VARIABLE = "variable";

    private static final String MESSAGE = "A %s category cannot be changed to a %s category.";
    private static final String MESSAGE_KEY = "exception.illegalUpdateCategoryClass";

    public IllegalUpdateCategoryClassException(Category category) {
        super(String.format(MESSAGE, category.isFixed() ? FIXED : VARIABLE, !category.isFixed() ? FIXED : VARIABLE),
                HttpStatus.BAD_REQUEST, MESSAGE_KEY, category.isFixed() ? FIXED : VARIABLE, !category.isFixed() ? FIXED : VARIABLE);
    }
}
