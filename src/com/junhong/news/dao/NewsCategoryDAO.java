/**
 * forum
 * zhanjung
 */
package com.junhong.news.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */
public class NewsCategoryDAO extends EntityDAOImpl<NewsCategory> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	public long getTotalCount() {
		return this.getEm().createQuery(QueryConstants.newscategory_total_count, Long.class).getSingleResult();

	}

	public long getTotalCount(int parentCategoryId) {
		return this.getEm().createQuery(QueryConstants.sub_forumcategory_total_count + parentCategoryId, Long.class).getSingleResult();

	}

	/**
	 * update the number of News for each category in the given list
	 * 
	 * @param categoryList
	 */
	public void updateNumberOfNews(Collection<NewsCategory> categoryList) {

		String updateQuery = "update NewsCategory c set  c.numOfNews=:num where c.id=:Id";
		Query query = this.getEm().createQuery(updateQuery);
		for (NewsCategory fc : categoryList) {
			query.setParameter("num", fc.getNumOfNews()).setParameter("Id", fc.getId()).executeUpdate();

		}
	}

	public List<NewsCategory> getNewsCategories(List<Integer> newsCategoryList) {
		String hql = "select nc from NewsCategory nc where nc.id is in (:idlist)";
		List<NewsCategory> newsCategoriesList = this.getEm().createQuery(hql, NewsCategory.class).setParameter("idlist", newsCategoryList).getResultList();
		return newsCategoriesList;

	}
	/* -------------getter/setter----------------- */
}
