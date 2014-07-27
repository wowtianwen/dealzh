/**
 * forum
 * zhanjung
 */
package com.junhong.forum.constaint;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.junhong.auth.service.UserEjb;

/**
 * @author zhanjung
 * 
 */
public class UserIdUniquenessCheckValidator implements ConstraintValidator<UserIdUniquenessCheck, String> {

	// -- it not only inject a reference, it also register JNDI ENC a entry inthe JNDI ENC
	@EJB(name = "userEjb")
	private UserEjb	userEjb;

	@Override
	public void initialize(UserIdUniquenessCheck constraintAnnotation) {
		try {
			userEjb = (UserEjb) new InitialContext().lookup("java:comp/env/userEjb");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !userEjb.isExist(value);
	}
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
}
