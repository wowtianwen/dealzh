package com.junhong.news.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.OwnerCheck;
import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.GenericCRUDService;
import com.junhong.news.dao.CommentDAO;
import com.junhong.news.entity.Comment;
import com.junhong.news.entity.News;

/**
 */
@Stateless
@LocalBean
public class CommentEjb extends GenericCRUDService<Comment> {

	@Inject
	private CommentDAO commentDAO;

	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger logger;

	@EJB
	private NewsEjb newsEjb;
	@EJB
	private UserEjb userEjb;

	/**
	 * Default constructor.
	 */
	public CommentEjb() {
	}

	/** business method */

	/**
	 * @param id
	 * @return
	 */
	public Comment getCommentById(int id) {
		return commentDAO.findById(Comment.class, id);
	}

	/**
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Comment> getCommentsByNews(News news) {
		List<Comment> commentList = null;
		commentList = commentDAO.getCommentsByNews(news);
		return commentList;
	}

	public List<Comment> getCommentsByNews(News thread, int start, int size) {
		List<Comment> commentList = null;
		commentList = commentDAO.getCommentsByNews(thread, start, size);
		return commentList;
	}

	/**
	 * 
	 * @param news
	 * @return
	 */
	public int getTotalCountOfComment(News news) {
		return (int) commentDAO.getTotalCount(news);
	}

	/**
	 * create a new Reply
	 * 
	 * @param comment
	 */
	@Role.List({ @Role(RoleType.REGISTERED) })
	@OwnerCheck()
	@Interceptors(AuthorizationInterceptor.class)
	public void createComment(Comment comment) throws AuthorizationFailException {
		commentDAO.create(comment);
		newsEjb.updateNumOfComments(comment.getNews(), +1);
	}

	/**
	 * 
	 * @param comment
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@OwnerCheck
	@Interceptors(AuthorizationInterceptor.class)
	public void updateComment(Comment comment) throws AuthorizationFailException {
		commentDAO.update(comment);
	}

	/**
	 * delete thread
	 * 
	 * @param comment
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteComment(Comment comment) throws AuthorizationFailException {
		newsEjb.updateNumOfComments(comment.getNews(), -1);
		commentDAO.delete(comment);
	}

}
