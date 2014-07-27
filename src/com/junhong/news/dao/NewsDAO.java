/**
 * forum
 * zhanjung
 */
package com.junhong.news.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */
public class NewsDAO extends EntityDAOImpl<News> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */
	/**
	 * this should filter out locked thread depending on the user's
	 * eligiability.
	 * 
	 * @param category
	 * @return
	 */
	public long getTotalCount(NewsCategory category, boolean isUserAccessableToLockedThreads) {
		String query = QueryConstants.GET_NEWS_COUNT_BY_CATEGORY;
		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query = query + " and news.locked=false";

		}
		return this.getEm().createQuery(query, Long.class).setParameter("category", category).getSingleResult();

	}

	public long getTotalCount(User owner, boolean isUserAccessableToLockedThreads) {
		String query = QueryConstants.GET_NEWS_COUNT_BY_OWNER;
		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query = query + " and ft.locked=false";

		}
		return this.getEm().createQuery(query, Long.class).setParameter("user", owner).getSingleResult();

	}

	public List<News> getNewsByCategory(NewsCategory newsCategory, int start, int size, List<Integer> excludeId, boolean isUserAccessableToLockedThreads, boolean excludeDashBoardNews) {

		StringBuilder GET_NEWS_LIST_BY_CATEGORY = new StringBuilder("select news from News news where news.newsCategory=:category ");

		boolean checkToppedNews = false;
		if (null != excludeId && excludeId.size() > 0) {
			GET_NEWS_LIST_BY_CATEGORY.append(" and news.id NOT IN (:excludeId)");
			checkToppedNews = true;
		}
		if (!isUserAccessableToLockedThreads) {
			GET_NEWS_LIST_BY_CATEGORY.append(" and news.locked=false");

		}
		if (excludeDashBoardNews) {
			GET_NEWS_LIST_BY_CATEGORY.append(" and news.onDashBoard=false");
		}
		GET_NEWS_LIST_BY_CATEGORY.append(" order by news.createTime desc");
		String hql = GET_NEWS_LIST_BY_CATEGORY.toString();

		TypedQuery<News> typedQuery = this.getEm().createQuery(hql, News.class).setParameter("category", newsCategory);
		if (checkToppedNews) {
			typedQuery = typedQuery.setParameter("excludeId", excludeId);
		}

		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * @param NewsCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<News> getNewsByOwner(User user, int start, int size, boolean isUserAccessableToLockedThreads) {

		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder("select news from News news where news.owner=:owner ");

		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and news.locked=false");

		}
		GET_THREAD_LIST_BY_CATEGORY.append(" order by news.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();

		TypedQuery<News> typedQuery = this.getEm().createQuery(hql, News.class).setParameter("owner", user);

		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	public void updateNumOfView(int id, int numOfHit) {
		this.getEm().createQuery(QueryConstants.UPDATE_NEWS_NUMOFVIEW).setParameter("numOfHit", numOfHit).setParameter("id", id).executeUpdate();

	}

	public void updateVotes(int id, int votes) {
		this.getEm().createQuery(QueryConstants.UPDATE_NEWS_VOTES).setParameter("votes", votes).setParameter("id", id).executeUpdate();

	}

	public List<News> getNewsByCategory(NewsCategory newsCategory) {
		return this.getEm().createQuery(QueryConstants.GET_NEWS_LIST_BY_CATEGORY, News.class).setParameter("category", newsCategory).getResultList();
	}

	public List<News> getToppedNewsList(NewsCategory newsCategory) {
		return this.getEm().createQuery(QueryConstants.GET_TOPPED_NEWS, News.class).setParameter("category", newsCategory).setParameter("isTopped", true).getResultList();
	}

	public List<News> getMostRecentNews(int size) {
		String hqlMostRecent = "select news from News news  where news.onDashBoard=?1 order by news.createTime desc ";
		return this.getEm().createQuery(hqlMostRecent, News.class).setParameter(1, false).setMaxResults(size).getResultList();
	}
	/* -------------getter/setter----------------- */
}
