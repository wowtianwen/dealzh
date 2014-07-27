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
@Constraint(validatedBy = PasswordMatchConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

	String first() ;
	String second();

	String message() default "{PassWordNOTMatch}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
