/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.Collection;

import javax.persistence.Query;

import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.entity.ForumCategory;

/**
 * @author zhanjung
 * 
 */
public class ForumCategoryDAO extends EntityDAOImpl<ForumCategory> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	public long getTotalCount() {
		return this.getEm().createQuery(QueryConstants.forumcategory_total_count, Long.class).getSingleResult();

	}

	public long getTotalCount(int parentCategoryId) {
		return this.getEm().createQuery(QueryConstants.sub_forumcategory_total_count + parentCategoryId, Long.class).getSingleResult();

	}

	public long getTotalNumberOfThead(ForumCategory category) {
		// TODO getTotalNumberOfThead in ForumCategoryDAO
		return 0;

	}

	/**
	 * update the number of thread for each category in the given list
	 * 
	 * @param categoryList
	 */
	public void updateNumberOfThead(Collection<ForumCategory> categoryList) {

		String updateQuery = "update ForumCategory c set  c.numOfThread=:num where c.id=:Id";
		Query query = this.getEm().createQuery(updateQuery);
		for (ForumCategory fc : categoryList) {
			query.setParameter("num", fc.getNumOfThread()).setParameter("Id", fc.getId()).executeUpdate();

		}

	}
	/* -------------getter/setter----------------- */
}
