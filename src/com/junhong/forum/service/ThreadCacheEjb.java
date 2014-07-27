package com.junhong.forum.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.forum.common.ThreadType;
import com.junhong.forum.dao.ForumThreadDAO;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.util.CommonUtil;

@Singleton
@Named
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@Lock(LockType.READ)
public class ThreadCacheEjb {
	@Inject
	private ForumThreadDAO					threadDAO;

	@EJB
	ThreadEjb								threadEjb;
	private Map<String, ForumCategory>		threadCategoryMap		= null;
	int										size					= 10;
	@EJB
	CategoryServiceSingleton				categoryEjb;
	private Map<String, List<ForumThread>>	recentPopularThreadsMap	= new ConcurrentHashMap<String, List<ForumThread>>();
	// get the most recent threads
	private List<ForumThread>				mostRecentThreadList	= new ArrayList<ForumThread>();
	private int								numberOfDeals4Today		= 0;

	/**
	 * get the most recent news for given category from the cache instead of the
	 * db
	 * 
	 * @param categoryName
	 * @return
	 */
	public List<ForumThread> getRecentPopularThreads(String categoryName, ForumThread excludeThreads) {
		if (recentPopularThreadsMap.isEmpty()) {
			populateRecentPopularThreads();
		}

		List<ForumThread> threadList = null;
		if (categoryName != null) {
			threadList = recentPopularThreadsMap.get(categoryName);
		}
		if (threadList == null) {
			threadList = new ArrayList<ForumThread>();
		}
		// remove and add the exclude thread to the end of the list, sothat it
		// wont be displayed
		threadList.remove(excludeThreads);
		threadList.add(excludeThreads);
		return threadList;

	}

	/**
	 * refresh cache from db for the overall news: not each category :used in
	 * the news index page
	 */
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/10", hour = "*",persistent = false)
	public void populateRecentPopularThreads() {
		if (threadCategoryMap == null || threadCategoryMap.isEmpty()) {
			categoryEjb.init();
			threadCategoryMap = categoryEjb.getCategoryOfThreadMap();
		}

		List<ForumThread> threadList = new ArrayList<ForumThread>();
		for (ForumCategory category : threadCategoryMap.values()) {
			threadList = threadDAO.getTrendingPopularThread(category, 10, 7); // get
																				// popular
																				// threads
																				// for
																				// past
																				// 3
																				// days
			recentPopularThreadsMap.put(category.getCategoryName(), threadList);
		}

		// get the most recent threads regardless any category
		mostRecentThreadList = threadEjb.getMostRecentThread(10);
		numberOfDeals4Today = retrieveNumberOfDeals4Today();

	}

	public List<ForumThread> getMostRecentThreadList() {
		return mostRecentThreadList;
	}

	public void setMostRecentThreadList(List<ForumThread> mostRecentThreadList) {
		this.mostRecentThreadList = mostRecentThreadList;
	}

	public int retrieveNumberOfDeals4Today() {
		Date today = CommonUtil.getESTDateWithoutTime(new Date());
		return threadEjb.getNumberOfDeals(today);
	}

	/* -------------getter/setter----------------- */

	public int getNumberOfDeals4Today() {
		if (numberOfDeals4Today == 0) {
			numberOfDeals4Today = retrieveNumberOfDeals4Today();
		}
		return numberOfDeals4Today;
	}

	public void setNumberOfDeals4Today(int numberOfDeals4Today) {
		this.numberOfDeals4Today = numberOfDeals4Today;
	}

}