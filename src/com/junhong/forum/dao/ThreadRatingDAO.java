/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.List;

import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadRating;

/**
 * @author zhanjung
 * 
 */
public class ThreadRatingDAO extends EntityDAOImpl<ForumThread> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	public boolean isExist(String userIdThreadId) {

		String hql = "select rating from ThreadRating rating where rating.userIdThreadId=:userIdThreadId";
		List<ThreadRating> result = this.getEm().createQuery(hql, ThreadRating.class).setParameter("userIdThreadId", userIdThreadId).setMaxResults(1).getResultList();
		if (result.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}
	/* -------------getter/setter----------------- */
}
