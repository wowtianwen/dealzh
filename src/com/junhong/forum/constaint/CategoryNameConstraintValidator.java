/**
 * forum
 * zhanjung
 */
package com.junhong.forum.constaint;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.service.CategoryServiceSingleton;

/**
 * @author zhanjung
 * 
 */
public class CategoryNameConstraintValidator implements
		ConstraintValidator<CategoryNameUniqueness, ForumCategory> {

	@EJB
	CategoryServiceSingleton categoryEjb;

	@Override
	public void initialize(CategoryNameUniqueness arg0) {
		try {

			categoryEjb = (CategoryServiceSingleton) new InitialContext()
					.lookup("java:global/forum/CategoryServiceSingleton");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public boolean isValid(ForumCategory category,
			ConstraintValidatorContext arg1) {

		return !categoryEjb.isCategoryExists(category);
	}
}
