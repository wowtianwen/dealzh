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
@Constraint(validatedBy = UserIdUniquenessCheckValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserIdUniquenessCheck {

	String message() default "{DuplicateUserId}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
