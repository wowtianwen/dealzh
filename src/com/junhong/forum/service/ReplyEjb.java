package com.junhong.forum.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.OwnerCheck;
import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.dao.ReplyDAO;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.exceptions.AuthorizationFailException;

/**
 * Session Bean implementation class ThreadEjb
 */
/**
 * @author zhanjung
 * 
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReplyEjb {

	@Inject
	private ReplyDAO replyDAO;

	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger logger;

	@EJB
	private ThreadEjb threadEjb;
	@EJB
	private UserEjb userEjb;
	@Inject
	private BuildLinkEjb buildLinkEjb;

	/**
	 * Default constructor.
	 */
	public ReplyEjb() {
	}

	/** business method */

	/**
	 * @param id
	 * @return
	 */
	public ForumReply getReplyById(int id) {
		return replyDAO.findById(ForumReply.class, id);
	}

	/**
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumReply> getReplyListByThread(ForumThread thread) {
		List<ForumReply> forumReplyList = null;
		logger.debug("Start ------ " + "getReplyListByThread ");
		forumReplyList = replyDAO.getRepliesByThread(thread);
		logger.debug("End ------ " + "getReplyListByThread ");
		return forumReplyList;
	}

	public List<ForumReply> getReplyListByThread(ForumThread thread, int start, int size) {
		List<ForumReply> forumReplyList = null;
		logger.debug("Start ------ " + "getReplyListByThread with start and size ");
		forumReplyList = replyDAO.getReplysByThead(thread, start, size);
		logger.debug("End ------ " + "getReplyListByThread with start and size");
		return forumReplyList;
	}

	public List<ForumReply> getReplyListByOwner(User user, int start, int size) {
		List<ForumReply> forumReplyList = null;
		forumReplyList = replyDAO.getReplysByOwner(user, start, size);
		return forumReplyList;
	}

	/**
	 * 
	 * @param thread
	 * @return
	 */
	public int getTotalCountOfReply(ForumThread thread) {
		return (int) replyDAO.getTotalCount(thread);
	}

	/**
	 * 
	 * @param thread
	 * @return
	 */
	public int getTotalCountOfReply(User owner) {
		return (int) replyDAO.getTotalCount(owner);
	}

	/**
	 * create a new Reply
	 * 
	 * @param forumReply
	 */
	@Role.List({ @Role(RoleType.REGISTERED), @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@OwnerCheck()
	@Interceptors(AuthorizationInterceptor.class)
	public void createForumReply(ForumReply forumReply) throws AuthorizationFailException {
		String content = forumReply.getContent();
		content = buildLinkEjb.buildAffiliateLink(content);
		forumReply.setContent(content);
		replyDAO.create(forumReply);
		threadEjb.updateNumOfRepliesNSetLastReply(forumReply.getThread(), forumReply, +1);
		userEjb.updateNumOfReplies(forumReply.getOwner(), +1);
	}

	/**
	 * update Reply
	 * 
	 * @param forumReply
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@OwnerCheck
	@Interceptors(AuthorizationInterceptor.class)
	public void updateForumReply(ForumReply forumReply) throws AuthorizationFailException {
		String content = forumReply.getContent();
		content = buildLinkEjb.buildAffiliateLink(content);
		forumReply.setContent(content);
		replyDAO.update(forumReply);
	}

	/**
	 * delete thread
	 * 
	 * @param forumReply
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteForumReply(ForumReply forumReply) throws AuthorizationFailException {
		threadEjb.updateNumOfRepliesNSetLastReplyForReplyDelete(forumReply.getThread(), forumReply, -1);
		replyDAO.delete(forumReply);

		userEjb.updateNumOfReplies(forumReply.getOwner(), -1);
	}

}
