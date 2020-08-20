package org.financer.server.application.api.error;

import org.financer.server.domain.model.transaction.Transaction;
import org.springframework.http.HttpStatus;

public class IllegalTransactionCategoryClassException extends RestException {

    private static final String MESSAGE = "Transaction %s has to be assigned to a %s category.";
    private static final String MESSAGE_KEY = "exception.illegalTransactionCategoryClass";

    public IllegalTransactionCategoryClassException(Transaction transaction) {
        super(String.format(MESSAGE, transaction.toString(), transaction.isFixed() ? "variable" : "fixed"),
                HttpStatus.BAD_REQUEST, MESSAGE_KEY, transaction.toString(), transaction.isFixed() ? "variable" : "fixed");
    }
}
