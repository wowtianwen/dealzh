/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.List;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;

/**
 * @author zhanjung
 * 
 */

public class ReplyDAO extends EntityDAOImpl<ForumReply> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/**
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumReply> getRepliesByThread(ForumThread thread) {
		return this.getEm().createQuery(QueryConstants.GET_REPLY_LIST_BY_THREAD, ForumReply.class).setParameter("parentThread", thread)
				.getResultList();
	}

	/**
	 * get the total number of reply for given thread
	 * 
	 * @param thread
	 * @return
	 */
	public long getTotalCount(ForumThread thread) {
		return this.getEm().createQuery(QueryConstants.GET_REPLY_COUNT_BY_THREAD, Long.class).setParameter("parentThread", thread).getSingleResult();
	}

	/**
	 * get the total number of reply for given owner
	 * 
	 * @param thread
	 * @return
	 */
	public long getTotalCount(User user) {
		return this.getEm().createQuery(QueryConstants.GET_REPLY_COUNT_BY_OWNER, Long.class).setParameter("user", user).getSingleResult();
	}

	// the reply list with start and size
	/**
	 * @param thread
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumReply> getReplysByThead(ForumThread thread, int start, int size) {
		return this.getEm().createQuery(QueryConstants.GET_REPLY_LIST_BY_THREAD, ForumReply.class).setParameter("parentThread", thread)
				.setFirstResult(start).setMaxResults(size).getResultList();
	}

	public List<ForumReply> getReplysByOwner(User owner, int start, int size) {
		return this.getEm().createQuery(QueryConstants.GET_REPLY_LIST_BY_OWNER, ForumReply.class).setParameter("user", owner).setFirstResult(start)
				.setMaxResults(size).getResultList();
	}
	/* -------------getter/setter----------------- */
}
