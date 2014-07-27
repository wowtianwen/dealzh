/**
 * forum
 * zhanjung
 */
package com.junhong.forum.constaint;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;

/**
 * @author zhanjung
 * 
 */
public class PasswordMatchConstraintValidator implements ConstraintValidator<PasswordMatch, Object> {

	private String	firstFieldName;
	private String	secondFieldName;

	@Inject
	Logger			logger;

	@Override
	public void initialize(PasswordMatch arg0) {

		firstFieldName = arg0.first();
		secondFieldName = arg0.second();

	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			String firstFieldValue = (String) BeanUtils.getProperty(value, firstFieldName);
			String secondFieldValue = (String) BeanUtils.getProperty(value, secondFieldName);

			if (firstFieldValue.equals(secondFieldValue)) {
				return true;
			}

		} catch (Exception ex) {
			logger.warn(ex.getMessage());

		}

		return false;
	}
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
}
