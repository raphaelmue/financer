package org.financer.util.validation;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class NotNullConditionalValidator implements ConstraintValidator<NotNullConditional, Object> {

    private String message;
    private String fieldName;
    private String expectedFieldValue;
    private String dependFieldName;

    @Override
    public void initialize(NotNullConditional annotation) {
        message = annotation.message();
        fieldName = annotation.fieldName();
        expectedFieldValue = annotation.fieldValue();
        dependFieldName = annotation.dependFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext ctx) {
        if (value == null) {
            return true;
        }

        try {
            String fieldValue = BeanUtils.getProperty(value, fieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);

            if (expectedFieldValue.equals(fieldValue) && dependFieldValue == null) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(dependFieldName)
                        .addConstraintViolation();
                return false;
            }

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        return true;
    }
}
