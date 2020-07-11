package org.financer.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that field {@code dependFieldName} is not null if field {@code fieldName} has value {@code fieldValue}.
 **/
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Repeatable(NotNullConditional.List.class)
@Constraint(validatedBy = NotNullConditionalValidator.class)
@Documented
public @interface NotNullConditional {

    String fieldName();

    String fieldValue();

    String dependFieldName();

    String message() default "{NotNullConditional.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        NotNullConditional[] value();
    }

}
