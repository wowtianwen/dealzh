/**
 * forum
 * zhanjung
 */
package com.junhong.forum.entity;

import java.util.Comparator;

/**
 * @author zhanjung
 * 
 */
public class ForumCategoryComparator implements Comparator<ForumCategory> {

	@Override
	public int compare(ForumCategory o1, ForumCategory o2) {
		// TODO Auto-generated method stub

		return o1.getDisplaySequence() - (o2.getDisplaySequence());
	}
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
}
