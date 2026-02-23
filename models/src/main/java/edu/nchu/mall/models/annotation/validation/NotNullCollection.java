package edu.nchu.mall.models.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.Collection;

@Documented
@Constraint(
        validatedBy = {NotNullCollection.NotNullCollectionValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullCollection {

    String message() default "{validation.collections.invalid.notnull:数组/集合元素不能为null}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class NotNullCollectionValidator implements ConstraintValidator<NotNullCollection, Collection<?>> {
        @Override
        public boolean isValid(Collection<?> s, ConstraintValidatorContext constraintValidatorContext) {
            return s != null && !s.contains(null);
        }
    }
}
