package org.financer.shared.exceptions;

import java.util.Arrays;

/**
 * Thrown when an enum is created from String, which can not be associated to any of the enum's values.
 */
public class EnumNotFoundException extends FinancerException {

    private static final String MESSAGE = "%s cannot be '%s'. Permitted values are: %s";
    private static final String MESSAGE_KEY = "exception.notFound.enum";

    public <E extends Enum<E>> EnumNotFoundException(Class<E> enumClass, String value) {
        super(String.format(MESSAGE, enumClass.getName(), value, Arrays.toString(enumClass.getDeclaringClass().getEnumConstants())),
                MESSAGE_KEY,
                enumClass.getName(), value, Arrays.toString(enumClass.getDeclaringClass().getEnumConstants()));
    }
}
