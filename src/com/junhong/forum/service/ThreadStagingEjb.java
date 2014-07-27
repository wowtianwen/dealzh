package com.junhong.forum.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.junhong.auth.annotation.Role;
import com.junhong.auth.common.Login;
import com.junhong.auth.entity.RoleType;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.common.RecordStatus;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadStaging;
import com.junhong.util.CommonUtil;
import com.junhong.util.StringUtil;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ThreadStagingEjb {
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<ThreadStaging>		entityDAO;

	@Inject
	private GenericCRUDService<ThreadStaging>	genCRUDService;
	@Inject
	private Login								login;
	@Inject
	private ThreadEjb							threadEjb;
	@Inject
	private CategoryServiceSingleton			categoryEjb;
	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger										logger;

	/**
	 * Default constructor.
	 */
	public ThreadStagingEjb() {
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalThreadStagingCount() {
		String hql = "select count(ts) from ThreadStaging ts";
		TypedQuery<Long> query = entityDAO.getEm().createQuery(hql, Long.class);
		int totalCount = query.getSingleResult().intValue();
		return totalCount;
	}

	public int getTotalThreadStagingCount(Map<String, Object> filters) {
		StringBuilder hql = new StringBuilder("select count(ts) from ThreadStaging ts where  ");
		String filterProperty = null;
		Object filterValue = null;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("createTime")) {
				hql.append(filterProperty).append(">='").append(filterValue).append("'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		TypedQuery<Long> query = entityDAO.getEm().createQuery(hql.toString(), Long.class);
		return query.getSingleResult().intValue();
	}

	@Role.List({ @Role(RoleType.SYSADMIN), @Role(RoleType.REGISTERED) })
	@Interceptors(AuthorizationInterceptor.class)
	public List<ThreadStaging> getThreadStagingList(int start, int size) {
		List<ThreadStaging> result = null;
		String hql = "select ts from ThreadStaging ts order by ts.createTime desc";
		result = entityDAO.getEm().createQuery(hql, ThreadStaging.class).setFirstResult(start).setMaxResults(size).getResultList();
		return result;
	}

	/**
	 * give the access for registered user for stagingthread, but
	 * threadstaginglist page only display for sysadmin and category owner
	 * 
	 * @param start
	 * @param size
	 * @param filters
	 * @return
	 */
	@Role.List({ @Role(RoleType.SYSADMIN), @Role(RoleType.REGISTERED) })
	@Interceptors(AuthorizationInterceptor.class)
	public List<ThreadStaging> getThreadStagingList(int start, int size, Map<String, Object> filters) {
		List<ThreadStaging> result = null;
		StringBuilder hql = new StringBuilder("select ts from ThreadStaging ts where  ");
		String filterProperty = null;
		Object filterValue = null;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("createTime")) {
				hql.append(filterProperty).append(">='").append(filterValue).append("'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}
		}
		hql.append(" order by createTime desc");
		result = entityDAO.getEm().createQuery(hql.toString(), ThreadStaging.class).setFirstResult(start).setMaxResults(size).getResultList();
		return result;
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createThreadStaging(ThreadStaging threadStaging) {
		genCRUDService.create(threadStaging);
	}

	public void createThreadStagingWithoutAuthorization(ThreadStaging threadStaging) {
		genCRUDService.create(threadStaging);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void updateThreadStaging(ThreadStaging threadStaging) {
		entityDAO.update(threadStaging);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void removeThreadStaging(ThreadStaging threadStaging) {
		entityDAO.delete(threadStaging);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public List<ThreadStaging> getThreadStaging(String externalId) {
		String hql = "select ts from ThreadStaging ts where ts.externalId=?1";
		return genCRUDService.findByHQL(hql, ThreadStaging.class, externalId);
	}

	public List<ThreadStaging> getThreadStagingWithoutAuthorization(String externalId) {
		String hql = "select ts from ThreadStaging ts where ts.externalId=?1";
		return genCRUDService.findByHQL(hql, ThreadStaging.class, externalId);
	}

	@Role.List({ @Role(RoleType.SYSADMIN), @Role(RoleType.REGISTERED) })
	@Interceptors(AuthorizationInterceptor.class)
	public void promoteThreadStaging(ThreadStaging threadStaging) {
		threadStaging.setStatus(RecordStatus.PROCESSED);
		this.updateThreadStaging(threadStaging);
		ForumThread thread = new ForumThread();
		thread.setSubject(threadStaging.getSubject());
		thread.setContent(threadStaging.getContent());
		thread.setStore(threadStaging.getStore());
		thread.setLastReplyTime(thread.getCreateTime());
		thread.setThumbPicURL(threadStaging.getThumbPicURL());
		thread.setOwner(login.getCurrentUser());
		if (!StringUtil.isNullOrBlank(threadStaging.getPrice())) {
			thread.setPrice(threadStaging.getPrice());
		}
		thread.setStatus(ThreadStatus.APPROVED);
		thread.setCategory(categoryEjb.getForumCategoryById(3L)); // default
																	// category
																	// to
																	// 3(laptop)
		// default the following values
		thread.setVotes(2);
		thread.setRating(2);
		thread.setNumberOfView(150);

		threadEjb.createForumThread(thread);

	}

	/**
	 * this method will delete threadstaging record older > 5days batch mode.
	 * will run every 3 days ondemand mode. called on demand
	 * 
	 * */
	@Timeout
	@Schedule(dayOfWeek = "Sun", month = "*", year = "*", second = "0", minute = "*", hour = "3", persistent = false)
	public void cleanStagingThreads() {
		// clean all staging thread older >7days
		int oldAge = 5;
		Date currESTDate = CommonUtil.getESTDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currESTDate);
		cal.add(Calendar.DATE, 0 - oldAge);
		Date date = cal.getTime();
		String hql = "delete from ThreadStaging ts where ts.createTime <:date";
		this.entityDAO.getEm().createQuery(hql).setParameter("date", date).executeUpdate();
	}
}
