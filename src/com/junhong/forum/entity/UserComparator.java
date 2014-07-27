/**
 * forum
 * zhanjung
 */
package com.junhong.forum.entity;

import java.util.Comparator;

import com.junhong.auth.entity.User;

/**
 * @author zhanjung
 * 
 */
public class UserComparator implements Comparator<User> {

	@Override
	public int compare(User o1, User o2) {
		// TODO Auto-generated method stub

		return o1.getUserId().compareTo(o2.getUserId());
	}
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
}
