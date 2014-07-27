/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.ThreadType;
import com.junhong.forum.entity.Event;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;

/**
 * @author zhanjung
 * 
 */
public class ForumThreadDAO extends EntityDAOImpl<ForumThread> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */
	/**
	 * this should filter out locked thread depending on the user's
	 * eligiability.
	 * 
	 * @param category
	 * @return
	 */
	public long getTotalCount(ForumCategory category, boolean isUserAccessableToLockedThreads) {
		StringBuilder query = new StringBuilder(QueryConstants.GET_THREAD_COUNT_BY_CATEGORY);
		query.append(" and ft.status =:status");
		query.append(" and ft.type =:type");
		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query.append(" and ft.locked=false");

		}

		return this.getEm().createQuery(query.toString(), Long.class).setParameter("status", ThreadStatus.APPROVED)
				.setParameter("type", ThreadType.REGULAR).setParameter("category", category).getSingleResult();

	}

	/**
	 * get total thread count for all category
	 * 
	 * @param category
	 * @param isUserAccessableToLockedThreads
	 * @return
	 */
	public long getTotalCount4AllCategory(boolean isUserAccessableToLockedThreads) {
		StringBuilder query = new StringBuilder(QueryConstants.GET_THREAD_COUNT_4_ALL_CATEGORY);
		query.append(" where ft.status =:status");
		query.append(" and ft.type =:type");
		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query.append(" and ft.locked=false");
		}
		return this.getEm().createQuery(query.toString(), Long.class).setParameter("status", ThreadStatus.APPROVED)
				.setParameter("type", ThreadType.REGULAR).getSingleResult();

	}

	public long getTotalCount4Event(boolean isUserAccessableToLockedThreads, Event parentEvent, ForumCategory category) {
		StringBuilder query = new StringBuilder(QueryConstants.GET_THREAD_COUNT_4_EVENT);
		if (category != null) {
			query.append(" and ft.category=:category");
		}
		query.append(" and  ft.status =:status");

		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query.append(" and ft.locked=false");
		}
		TypedQuery<Long> query2 = this.getEm().createQuery(query.toString(), Long.class).setParameter("event", parentEvent);
		if (category != null) {
			query2.setParameter("category", category);
		}
		return query2.setParameter("status", ThreadStatus.APPROVED).getSingleResult();

	}

	public long getTotalCount(User owner, ThreadStatus status, boolean isUserAccessableToLockedThreads) {
		StringBuilder query = new StringBuilder(QueryConstants.GET_THREAD_COUNT_BY_OWNER);
		// check if the user is accessible to locked thread. only the category
		// owner or sysadmin do
		if (!isUserAccessableToLockedThreads) {
			query.append(" and ft.locked=false");
		}
		return this.getEm().createQuery(query.toString(), Long.class).setParameter("user", owner).setParameter("status", status).getSingleResult();

	}

	/**
	 * get thead for given forum category
	 * 
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreadsByCategory(ForumCategory forumCategory, int start, int size, List<Integer> excludeThreadsId,
			boolean isUserAccessableToLockedThreads) {
		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder(
				"select ft from ForumThread ft where ft.category=:category  and ft.status=:status  and ft.type=:type");
		boolean checkStickyThread = false;
		if (null != excludeThreadsId && excludeThreadsId.size() > 0) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.id NOT IN (:stickyThreadsId)");
			checkStickyThread = true;
		}
		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.locked=false");
		}
		GET_THREAD_LIST_BY_CATEGORY.append(" order by ft.lastReplyTime desc, ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();
		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class).setParameter("category", forumCategory);
		if (checkStickyThread) {
			typedQuery = typedQuery.setParameter("stickyThreadsId", excludeThreadsId);
		}
		typedQuery.setParameter("status", ThreadStatus.APPROVED);
		typedQuery.setParameter("type", ThreadType.REGULAR);
		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get threads for all forum category
	 * 
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreads4AllCategory(int start, int size, List<Integer> excludeThreadsId, boolean isUserAccessableToLockedThreads) {
		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder(
				"select ft from ForumThread ft  where 1=1 and ft.status=:status and ft.type=:type "); // 1=1
		// is
		// trick
		boolean checkStickyThread = false;
		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and  ft.locked=false");
		}
		if (null != excludeThreadsId && excludeThreadsId.size() > 0) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and  ft.id NOT IN (:stickyThreadsId)");
			checkStickyThread = true;

		}

		GET_THREAD_LIST_BY_CATEGORY.append(" order by ft.lastReplyTime desc, ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();

		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class);
		if (checkStickyThread) {
			typedQuery = typedQuery.setParameter("stickyThreadsId", excludeThreadsId);
		}
		typedQuery.setParameter("status", ThreadStatus.APPROVED);
		typedQuery.setParameter("type", ThreadType.REGULAR);
		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get threads for given Event
	 * 
	 * @param event
	 * @param start
	 * @param size
	 * @param excludeThreadsId
	 * @param isUserAccessableToLockedThreads
	 * @return
	 */
	public List<ForumThread> getThreadsByEvent(Event event, ForumCategory category, int start, int size, List<Integer> excludeThreadsId,
			boolean isUserAccessableToLockedThreads) {

		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder("select ft from ForumThread ft where ft.event=:event and ft.status=:status ");

		if (category != null) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.category=:category");
		}

		boolean checkStickyThread = false;
		if (null != excludeThreadsId && excludeThreadsId.size() > 0) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.id NOT IN (:stickyThreadsId)");
			checkStickyThread = true;

		}
		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.locked=false");

		}
		GET_THREAD_LIST_BY_CATEGORY.append(" order by ft.lastReplyTime desc, ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();

		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class).setParameter("event", event);
		if (category != null) {
			typedQuery.setParameter("category", category);
		}
		if (checkStickyThread) {
			typedQuery = typedQuery.setParameter("stickyThreadsId", excludeThreadsId);
		}
		typedQuery.setParameter("status", ThreadStatus.APPROVED);
		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get thread for all thread categories.
	 * 
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreadList4AllCategory(int start, int size, boolean isUserAccessableToLockedThreads) {

		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder("select ft from ForumThread ft  ");

		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" where ft.locked=false");

		}
		GET_THREAD_LIST_BY_CATEGORY.append(" order by ft.lastReplyTime desc, ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();

		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class);

		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get thread list for given event
	 * 
	 * @param start
	 * @param size
	 * @param isUserAccessableToLockedThreads
	 * @param parentEvent
	 * @return
	 */
	public List<ForumThread> getThreadList4Event(int start, int size, boolean isUserAccessableToLockedThreads, Event parentEvent) {

		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder("select ft from ForumThread ft where  ");

		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append("  ft.locked=false and ");

		}
		GET_THREAD_LIST_BY_CATEGORY.append("  ft.event=:event order by ft.lastReplyTime desc, ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();
		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class);
		return typedQuery.setParameter("event", parentEvent).setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreadsByOwner(User user, ThreadStatus status, int start, int size, boolean isUserAccessableToLockedThreads) {

		StringBuilder GET_THREAD_LIST_BY_CATEGORY = new StringBuilder("select ft from ForumThread ft where ft.owner=:owner and ft.status=:status ");

		if (!isUserAccessableToLockedThreads) {
			GET_THREAD_LIST_BY_CATEGORY.append(" and ft.locked=false");

		}
		GET_THREAD_LIST_BY_CATEGORY.append(" order by ft.createTime desc");
		String hql = GET_THREAD_LIST_BY_CATEGORY.toString();

		TypedQuery<ForumThread> typedQuery = this.getEm().createQuery(hql, ForumThread.class).setParameter("owner", user)
				.setParameter("status", status);

		return typedQuery.setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get popular thread by its vote for the past x days
	 * 
	 * @param size
	 * @param pastXDays
	 * @return
	 */
	public List<ForumThread> getTrendingPopularThread(int size, int pastXDays) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0 - pastXDays);
		Date date = calendar.getTime();
		String hqlMostRecent = "select threads from ForumThread threads  where threads.onDashBoard=false and threads.createTime>=:date order by threads.rating desc, threads.createTime desc  ";
		TypedQuery<ForumThread> query = this.getEm().createQuery(hqlMostRecent, ForumThread.class).setParameter("date", date);
		return query.setMaxResults(size).getResultList();
	}

	/**
	 * get popular thread by its vote for the past x days for the given category
	 * 
	 * @param size
	 * @param pastXDays
	 * @return
	 */
	public List<ForumThread> getTrendingPopularThread(ForumCategory category, int size, int pastXDays) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0 - pastXDays);
		Date date = calendar.getTime();
		String hqlMostRecent = "select threads from ForumThread threads  where threads.category.id=:categoryId and threads.createTime>=:date order by threads.rating desc, threads.createTime desc  ";
		TypedQuery<ForumThread> query = this.getEm().createQuery(hqlMostRecent, ForumThread.class).setParameter("categoryId", category.getId())
				.setParameter("date", date);
		return query.setMaxResults(size).getResultList();
	}

	public void updateNumOfView(int id, int numOfHit) {
		this.getEm().createQuery(QueryConstants.UPDATE_THREAD_NUMOFVIEW).setParameter("numOfHit", numOfHit).setParameter("id", id).executeUpdate();

	}

	public void updateVotesNRating(int id, int votes, int rating) {
		this.getEm().createQuery(QueryConstants.UPDATE_THREAD_VOTESNRATING).setParameter("votes", votes).setParameter("rating", rating)
				.setParameter("id", id).executeUpdate();

	}

	public List<ForumThread> getThreadsByCategory(ForumCategory forumCategory) {
		return this.getEm().createQuery(QueryConstants.GET_THREAD_LIST_BY_CATEGORY, ForumThread.class).setParameter("category", forumCategory)
				.getResultList();
	}

	public List<ForumThread> getToppedThreadList(ForumCategory forumCategory) {
		return this.getEm().createQuery(QueryConstants.GET_TOPPED_THREAD, ForumThread.class).setParameter("category", forumCategory)
				.setParameter("status", ThreadStatus.APPROVED).setParameter("type", ThreadType.REGULAR).setParameter("isTopped", true)
				.getResultList();
	}

	public List<ForumThread> getToppedThreadList4AllCategory() {
		return this.getEm().createQuery(QueryConstants.GET_TOPPED_THREAD_4_ALL_CATEGORY, ForumThread.class).setParameter("isTopped", true)
				.setParameter("status", ThreadStatus.APPROVED).setParameter("type", ThreadType.REGULAR).getResultList();
	}

	public List<ForumThread> getToppedThreadList(Event event) {
		return this.getEm().createQuery(QueryConstants.GET_TOPPED_THREAD_EVENT, ForumThread.class).setParameter("event", event)
				.setParameter("isTopped", true).setParameter("status", ThreadStatus.APPROVED).getResultList();
	}

	/**
	 * get last reply Id for the given thread
	 * 
	 * @param thread
	 * @return
	 */
	@Deprecated
	public Integer getLastReplyId(ForumThread thread) {

		String hql = "select max(r.id) from ForumReply r where r.thread.id=:threadId ";
		List<Integer> maxIdList = this.getEm().createQuery(hql, Integer.class).setParameter("threadId", thread.getId()).getResultList();
		if (maxIdList.isEmpty()) {
			return null;
		}
		return maxIdList.get(0);

	}
	/* -------------getter/setter----------------- */
}
