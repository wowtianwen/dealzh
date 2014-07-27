/**
 * forum
 * zhanjung
 */
package com.junhong.news.dao;

import java.util.List;

import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.news.entity.Comment;
import com.junhong.news.entity.News;

/**
 * @author zhanjung
 * 
 */

public class CommentDAO extends EntityDAOImpl<Comment> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/**
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Comment> getCommentsByNews(News news) {
		return this.getEm().createQuery(QueryConstants.GET_COMMENT_LIST_BY_NEWS, Comment.class).setParameter("news", news).getResultList();
	}

	/**
	 * get the total number of reply for given thread
	 * 
	 * @param news
	 * @return
	 */
	public long getTotalCount(News news) {
		return this.getEm().createQuery(QueryConstants.GET_COMMENT_COUNT_BY_NEWS, Long.class).setParameter("news", news).getSingleResult();
	}

	// the reply list with start and size
	/**
	 * @param news
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Comment> getCommentsByNews(News news, int start, int size) {
		return this.getEm().createQuery(QueryConstants.GET_COMMENT_LIST_BY_NEWS, Comment.class).setParameter("news", news).setFirstResult(start).setMaxResults(size).getResultList();
	}

	/* -------------getter/setter----------------- */
}
