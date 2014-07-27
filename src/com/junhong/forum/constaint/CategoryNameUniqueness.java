/**
 * forum
 * zhanjung
 */
package com.junhong.forum.constaint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author zhanjung
 * 
 */
@Documented
@Constraint(validatedBy = CategoryNameConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryNameUniqueness {

    String first() default "id";

    String second() default "name";

    String message() default "{CategoryNameUniqueMessage}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface list {
	CategoryNameUniqueness[] value();

    }

}
