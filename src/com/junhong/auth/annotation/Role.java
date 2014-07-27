/**
 * forum
 * zhanjung
 */
package com.junhong.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.junhong.auth.entity.RoleType;

/**
 * @author zhanjung
 * 
 */
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
	RoleType value() default RoleType.GUEST;

	/**
	 * Defines several @ZipCode annotations on the same element
	 * 
	 * @see (@link ZipCode}
	 */
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		Role[] value();
	}

}
