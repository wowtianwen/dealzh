package com.junhong.forum.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.junhong.auth.annotation.OwnerCheck;
import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.ThreadType;
import com.junhong.forum.dao.ForumThreadDAO;
import com.junhong.forum.dao.ReplyDAO;
import com.junhong.forum.entity.Event;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadDashBoard;
import com.junhong.forum.entity.ThreadRating;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.stats.CacheKey;
import com.junhong.forum.stats.CacheType;
import com.junhong.forum.stats.RatingCache;
import com.junhong.util.CommonUtil;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ThreadEjb {

	@Inject
	private ForumThreadDAO		threadDAO;
	@Inject
	private ReplyDAO			replyDAO;

	@EJB
	private DashBoardService	dashBoardEjb;
	@EJB
	private RatingCache			ratingCache;
	@EJB
	private ThreadRatingEjb		ratingEjb;
	@EJB
	private BuildLinkEjb		buildLinkEjb;

	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger						logger;

	/**
	 * Default constructor.
	 */
	public ThreadEjb() {
	}

	/** business method */

	/**
	 * @param id
	 * @return
	 */
	public ForumThread getForumThreadById(int id) {
		return threadDAO.findById(ForumThread.class, id);
	}

	/**
	 * @param id
	 * @return
	 */
	public ForumThread getLoadedThreadById(int id) {
		ForumThread loadedThread = threadDAO.findById(ForumThread.class, id);
		loadedThread.getForumReplyList().size();
		return loadedThread;
	}

	/**
	 * @param category
	 * @return
	 */
	public List<ForumThread> getForumThreadListByCategory(ForumCategory category) {
		return threadDAO.getThreadsByCategory(category);
	}

	/**
	 * @param category
	 * @return
	 */
	public int getForumThreadCountByCategory(ForumCategory category, boolean isUserAccessableToLockedThreads) {
		return (int) threadDAO.getTotalCount(category, isUserAccessableToLockedThreads);
	}

	/**
	 * get total count for all thread Category
	 * 
	 * @param category
	 * @return
	 */
	public int getForumThreadCount4AllCategory(boolean isUserAccessableToLockedThreads) {
		return (int) threadDAO.getTotalCount4AllCategory(isUserAccessableToLockedThreads);
	}

	public int getNonApprovedForumThreadCount4AllPCategory(Map<String, Object> filters) {
		StringBuilder hql = new StringBuilder("select count(thread) from ForumThread thread where 1=1 ");
		if (!filters.isEmpty()) {
			hql.append(" and ");
		} else {
			hql.append(" and status='").append(ThreadStatus.PENDINGREVIEW.toString()).append("'");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
				if (ite.hasNext()) {
					hql.append(" and ");
				}

			}
		}
		return this.threadDAO.findByHQLSingleResultParmMethod(hql.toString(), Long.class).intValue();
	}

	public List<ForumThread> getNonApprovedForumThread4AllPCategory(int start, int size, Map<String, Object> filters) {
		StringBuilder hql = new StringBuilder("select thread from ForumThread thread where 1=1 ");
		if (!filters.isEmpty()) {
			hql.append(" and ");
		} else {
			hql.append(" and status='").append(ThreadStatus.PENDINGREVIEW.toString()).append("'");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
				if (ite.hasNext()) {
					hql.append(" and ");
				}

			}
		}
		hql.append(" order by thread.createTime desc ");
		return this.threadDAO.findByHQL(hql.toString(), ForumThread.class, start, size);
	}

	/**
	 * get total number threads for the given category, if category is null, then get all threads for the given event
	 * 
	 * @param isUserAccessableToLockedThreads
	 * @param parentEvent
	 * @param category
	 * @return
	 */
	public int getForumThreadCount4Event(boolean isUserAccessableToLockedThreads, Event parentEvent, ForumCategory category) {
		return (int) threadDAO.getTotalCount4Event(isUserAccessableToLockedThreads, parentEvent, category);
	}

	/**
	 * @param category
	 * @return
	 */
	public int getForumThreadCountByOwner(User owner, ThreadStatus status, boolean isUserAccessableToLockedThreads) {
		return (int) threadDAO.getTotalCount(owner, status, isUserAccessableToLockedThreads);
	}

	/**
	 * get thread for given forum category
	 * 
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreadListByCategory(ForumCategory forumCategory, int start, int size, List<Integer> excludeshreadsId,
			boolean isUserAccessableToLockedThreads) {
		List<ForumThread> forumThreadList = null;
		logger.debug("Start ------ " + "getThreadListByCategory ");
		forumThreadList = threadDAO.getThreadsByCategory(forumCategory, start, size, excludeshreadsId, isUserAccessableToLockedThreads);
		logger.debug("End ------ " + "getThreadListByCategory ");
		return forumThreadList;
	}

	public List<ForumThread> getThreadList4AllCategory(int start, int size, List<Integer> excludeshreadsId, boolean isUserAccessableToLockedThreads) {
		List<ForumThread> forumThreadList = null;
		forumThreadList = threadDAO.getThreads4AllCategory(start, size, excludeshreadsId, isUserAccessableToLockedThreads);
		return forumThreadList;
	}

	public List<ForumThread> getThreadListByEvent(Event event, ForumCategory category, int start, int size, List<Integer> excludeshreadsId,
			boolean isUserAccessableToLockedThreads) {
		List<ForumThread> forumThreadList = null;
		forumThreadList = threadDAO.getThreadsByEvent(event, category, start, size, excludeshreadsId, isUserAccessableToLockedThreads);
		return forumThreadList;
	}

	/**
	 * get thread for all Forum Category
	 * 
	 * @param forumCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> getThreadList4AllCategory(int start, int size, boolean isUserAccessableToLockedThreads) {
		List<ForumThread> forumThreadList = null;
		forumThreadList = threadDAO.getThreadList4AllCategory(start, size, isUserAccessableToLockedThreads);
		return forumThreadList;
	}

	public List<ForumThread> getThreadList4Event(int start, int size, boolean isUserAccessableToLockedThreads, Event parentEvent) {
		List<ForumThread> forumThreadList = null;
		forumThreadList = threadDAO.getThreadList4Event(start, size, isUserAccessableToLockedThreads, parentEvent);
		return forumThreadList;
	}

	/**
	 * get all the threads for the given User
	 * 
	 * @param user
	 * @param start
	 * @param size
	 * @param excludeshreadsId
	 * @param isUserAccessableToLockedThreads
	 * @return
	 */
	public List<ForumThread> getThreadList(User user, ThreadStatus status, int start, int size) {
		List<ForumThread> forumThreadList = null;
		forumThreadList = threadDAO.getThreadsByOwner(user, status, start, size, false);
		return forumThreadList;
	}

	/**
	 * create a new Thread
	 * 
	 * @param forumThread
	 */

	@Role.List({ @Role(RoleType.REGISTERED), @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createForumThread(ForumThread forumThread) {
		threadDAO.create(forumThread);
		String content = forumThread.getContent();
		content = buildLinkEjb.buildAffiliateLinkNPopStore(content, forumThread);
		forumThread.setContent(content);
		threadDAO.update(forumThread);
	}

	/**
	 * update Thread
	 * 
	 * @param forumThread
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@OwnerCheck
	@Interceptors(AuthorizationInterceptor.class)
	public void updateForumThread(ForumThread forumThread) throws AuthorizationFailException {
		String content = forumThread.getContent();
		content = buildLinkEjb.buildAffiliateLinkNPopStore(content, forumThread);
		forumThread.setContent(content);
		threadDAO.update(forumThread);
	}

	public void updateForumThreadWithoutAuthorization(ForumThread forumThread) {
		String content = forumThread.getContent();
		content = buildLinkEjb.buildAffiliateLinkNPopStore(content, forumThread);
		forumThread.setContent(content);
		threadDAO.update(forumThread);
	}

	/**
	 * delete thread
	 * 
	 * @param forumThread
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteForumThread(ForumThread forumThread) throws AuthorizationFailException {
		// delete internalLink before delte thread
		String hql = "delete from InternalLink link where link.thread=:thread";
		this.threadDAO.getEm().createQuery(hql).setParameter("thread", forumThread).executeUpdate();
		threadDAO.delete(forumThread);
	}

	public void refreshThead(ForumThread forumThread) {
		threadDAO.refresh(forumThread);
	}

	public void updateNumOfView(int id, int numOfHit) {
		threadDAO.updateNumOfView(id, numOfHit);
	}

	/**
	 * @param thread
	 */
	public ForumThread findByIdWithPessimisticWrite(int id) {

		return threadDAO.findByIdWithPessimisticWrite(ForumThread.class, id);

	}

	/**
	 * use pessimistic write locking to lock the row so that numofReplies can be update sequencially while it still can be read
	 * 
	 * @param thread
	 */
	@Asynchronous
	public void updateNumOfRepliesNSetLastReply(ForumThread thread, ForumReply lastReply, int delta) {

		thread = findByIdWithPessimisticWrite(thread.getId());
		ForumReply newLastReply = lastReply;
		// for delete, set it to null. because it is gonna to be slow if search
		// the previous one
		if (delta == -1) {
			// delete last reply
			if (thread.getLastReply().equals(lastReply)) {
				ForumReply currentLastReply = getLastReply(thread);
				thread.setLastReply(currentLastReply);
				if (currentLastReply == null) {
					thread.setLastReplyTime(null);
				} else {
					thread.setLastReplyTime(currentLastReply.getCreateTime());
				}
			}
		} else {
			// for create,
			thread.setLastReply(newLastReply);
			thread.setLastReplyTime(lastReply.getCreateTime());
		}

		thread.setNumberOfReplies(thread.getNumberOfReplies() + delta);
		// mark if thread should be marked hot. the parameter can be changed
		if (thread.isMarkedHot() == false && thread.getNumberOfReplies() >= 3) {
			thread.setMarkedHot(true);

		}
		threadDAO.update(thread);

	}

	/**
	 * use pessimistic write locking to lock the row so that numofReplies can be update sequencially while it still can be read
	 * 
	 * can not be asynchronous
	 * 
	 * @param thread
	 */
	public void updateNumOfRepliesNSetLastReplyForReplyDelete(ForumThread thread, ForumReply lastReply, int delta) {

		thread = findByIdWithPessimisticWrite(thread.getId());
		// for delete, set it to null. because it is gonna to be slow if search
		// the previous one
		// delete last reply
		if (thread.getLastReply() != null && thread.getLastReply().equals(lastReply)) {
			thread.setLastReply(null);
			thread.setLastReplyTime(null);
		}
		int newValue = thread.getNumberOfReplies() + delta;
		thread.setNumberOfReplies(newValue < 0 ? 0 : newValue);
		threadDAO.update(thread);
	}

	/**
	 * get the last reply for the given thread
	 * 
	 * @param thread
	 * @return
	 */
	@Deprecated
	public ForumReply getLastReply(ForumThread thread) {
		ForumReply lastReply = null;
		Integer lastReplyId = threadDAO.getLastReplyId(thread);
		if (lastReplyId != null) {
			lastReply = replyDAO.findById(ForumReply.class, lastReplyId);
		}
		return lastReply;
	}

	public void updateVotesNRating(int id, int votes, int rating) {
		threadDAO.updateVotesNRating(id, votes, rating);
	}

	public ForumThread getThreadWithLoadedStickiedByUserById(int id) {
		ForumThread loadedThread = threadDAO.findById(ForumThread.class, id);
		// loadedThread.getStickiedByUsers().size();
		return loadedThread;
	}

	public List<ForumThread> getToppedThreadList(ForumCategory category) {

		return threadDAO.getToppedThreadList(category);

	}

	public List<ForumThread> getToppedThreadList4AllCategory() {

		return threadDAO.getToppedThreadList4AllCategory();

	}

	public List<ForumThread> getToppedThreadList(Event event) {

		return threadDAO.getToppedThreadList(event);

	}

	/**
	 * put thread on the main dashboard
	 * 
	 * @param thread
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void dashBoardThread(ForumThread thread) throws AuthorizationFailException {

		thread = this.findByIdWithPessimisticWrite(thread.getId());
		thread.setOnDashBoard(true);
		this.updateForumThread(thread);
		// insert it into the dashboard table
		ThreadDashBoard db = new ThreadDashBoard();
		db.setCategoryId(thread.getCategory().getId());
		db.setThreadId(thread.getId());
		db.setThreadSubject(thread.getSubject());
		db.setThreadOwner(thread.getOwner().getUserId());
		db.setPicturePathURL(thread.getThumbPicURL());
		db.setPrice(thread.getPrice());
		db.setMarkedHot(thread.isMarkedHot());
		db.setShortContent(thread.getShortContent());
		db.setFullContent(thread.getContent());
		if (null != thread.getStore()) {
			db.setStoreName(thread.getStore().getName());
			db.setStore(thread.getStore());
		}
		dashBoardEjb.create(db);

	}

	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void unDashBoardThread(ForumThread thread) throws AuthorizationFailException {
		if (!thread.isOnDashBoard()) {
			return;
		}
		thread = this.findByIdWithPessimisticWrite(thread.getId());
		thread.setOnDashBoard(false);
		this.updateForumThread(thread);

		// remove it from dashboard table
		List<ThreadDashBoard> dashBoardList = dashBoardEjb.getDashBoard(thread.getId());
		for (ThreadDashBoard dashBoard : dashBoardList) {
			if (dashBoard != null) {
				dashBoardEjb.delete(dashBoard);
			}
		}

	}

	public void unDashBoardThreadWithoutAuthorization(ForumThread thread) {

		if (!thread.isOnDashBoard()) {
			return;
		}
		thread = this.findByIdWithPessimisticWrite(thread.getId());
		thread.setOnDashBoard(false);
		this.updateForumThreadWithoutAuthorization(thread);

		// remove it from dashboard table
		List<ThreadDashBoard> dashBoardList = dashBoardEjb.getDashBoard(thread.getId());
		for (ThreadDashBoard dashBoard : dashBoardList) {
			if (dashBoard != null) {
				dashBoardEjb.delete(dashBoard);
			}
		}

	}

	public List<ForumThread> getMostRecentThread(int size) {
		String hqlMostRecent = "select threads from ForumThread threads  where threads.onDashBoard=false order by threads.createTime desc ";
		return threadDAO.findByHQL(hqlMostRecent, ForumThread.class, 0, size);
	}

	public List<ForumThread> getTrendingPopularThread(int size, int pastXDays) {
		return threadDAO.getTrendingPopularThread(size, pastXDays);
	}

	public List<ForumThread> getTrendingPopularThread(ForumCategory category, int size, int pastXDays) {
		return threadDAO.getTrendingPopularThread(category, size, pastXDays);
	}

	/**
	 * 1. check if user is login and is already voted 2. if not, go and update 3. otherwise, throw error message
	 * 
	 * @param rating
	 */
	public boolean handleVotes(User currUser, ForumThread thread, int rating, boolean isUserSysadmin) {
		boolean validVote = false;
		String userIdThreadId = currUser.getId() + "" + thread.getId();
		if (isUserSysadmin || !ratingEjb.isExist(userIdThreadId)) {
			ratingCache.incrementAndGet(new CacheKey(thread.getId(), CacheType.THREAD), rating);
			if (!isUserSysadmin) {
				ratingEjb.create(new ThreadRating(userIdThreadId));
			}
			validVote = true;
		}
		return validVote;

	}

	/**
	 * get the number of deals for given date
	 * 
	 * @param date
	 * @return
	 */
	public int getNumberOfDeals(Date date) {
		String hql = "select count(thread) from ForumThread thread where thread.createTime>=:date";
		TypedQuery<Long> query = threadDAO.getEm().createQuery(hql, Long.class);
		query.setParameter("date", date);
		Integer result = query.getSingleResult().intValue();
		return result;

	}

	/**
	 * undashBorad all threads which are older than xDaysOld
	 * 
	 * @param xDaysOld
	 */
	public void cleanDashBoardThreads(int xDaysOld) {
		Date currESTDate = CommonUtil.getESTDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currESTDate);
		cal.add(Calendar.DATE, 0 - xDaysOld);
		Date date = cal.getTime();
		String hql = "select thread from ForumThread thread where thread.onDashBoard=:onDashBoard and thread.createTime < :xDaysOldDate ";
		TypedQuery<ForumThread> query = threadDAO.getEm().createQuery(hql, ForumThread.class);
		query.setParameter("onDashBoard", true);
		query.setParameter("xDaysOldDate", date);
		List<ForumThread> lists = query.getResultList();
		for (ForumThread thread : lists) {
			unDashBoardThreadWithoutAuthorization(thread);
		}

	}

	public void updateStatus(ForumThread thread, ThreadStatus status) throws AuthorizationFailException {
		ForumThread latestThread = this.findByIdWithPessimisticWrite(thread.getId());
		latestThread.setStatus(status);
		latestThread.setReviewedBy(thread.getReviewedBy());
		updateForumThread(latestThread);
	}

	/**
	 * get all announce threads regardless it is category
	 * 
	 * @return
	 */
	public List<ForumThread> getAllAnnounceThreads() {
		String hql = "select t from ForumThread t where t.type=?1 order by createTime asc";
		return threadDAO.findByHQL(hql, ForumThread.class, ThreadType.ANNOUNCE);

	}
}
