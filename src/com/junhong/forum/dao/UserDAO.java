/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.List;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.QueryConstants;

/**
 * @author zhanjung
 * 
 */
public class UserDAO extends EntityDAOImpl<User> {

	/* -------------business logic----------------- */

	public List<String> getUserIds(String prefix) {
		return this.getEm().createQuery(QueryConstants.GET_USER_IDS_BY_PREFIX, String.class)
				.setParameter("prefix", prefix+"%").getResultList();
	}

}
