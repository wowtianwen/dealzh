package com.junhong.forum.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumCategoryComparator;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadDashBoard;
import com.junhong.forum.entity.ThreadDashBoardComparator;

/**
 * Session Bean implementation class DashBoardService
 */

@Named
@ApplicationScoped
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class ThreadDashBoardCacheService {

	@EJB
	private CategoryServiceSingleton					categoryEjb;
	private Map<ForumCategory, List<ThreadDashBoard>>	categoryThreadsMap			= new ConcurrentHashMap<ForumCategory, List<ThreadDashBoard>>();
	// used in the /category/index.xhtml page
	private ForumCategory[]								allCategoryArray			= null;
	// used in the main index page. it include all Deals as 1st category. used in the tabs
	private List<ForumCategory>							allDBCategoryWithAllDealCat	= new ArrayList<ForumCategory>();
	@EJB
	private GenericCRUDService<ThreadDashBoard>			genericCRUDService;
	@EJB
	private ThreadEjb									threadEjb;
	private List<ThreadDashBoard>						allDashBoardThreads			= null;
	private List<ForumThread>							trendingPopularThreadList	= new ArrayList<ForumThread>();

	/**
	 * Default constructor.
	 */
	public ThreadDashBoardCacheService() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void init() {

		categoryThreadsMap.clear();
		allDBCategoryWithAllDealCat.clear();
		allDashBoardThreads = genericCRUDService.findAll(ThreadDashBoard.class);

		int categoryId = -999999;
		int tempCategoryId = 0;
		List<ThreadDashBoard> dashbaords = null;
		ForumCategory category = null;
		for (ThreadDashBoard db : allDashBoardThreads) {
			// check the CategoryID is changed
			tempCategoryId = db.getCategoryId();
			if (categoryId != tempCategoryId) {
				categoryId = tempCategoryId;
				dashbaords = new ArrayList<ThreadDashBoard>();
				category = categoryEjb.getForumCategoryById(tempCategoryId);
				if (category != null) {
					categoryThreadsMap.put(category, dashbaords);
				}

				dashbaords.add(db);
			} else {
				dashbaords.add(db);
			}
		}

		// populate category name array.
		Set<ForumCategory> result = categoryThreadsMap.keySet();
		allDBCategoryWithAllDealCat.addAll(result);
		Collections.sort(allDBCategoryWithAllDealCat, new ForumCategoryComparator());
		this.allCategoryArray = new ForumCategory[allDBCategoryWithAllDealCat.size()];
		allDBCategoryWithAllDealCat.toArray(allCategoryArray);

		// add All deals category
		ForumCategory category1 = new ForumCategory();
		category1.setId(Constants.CategoryId_For_All_Threads);
		String allDeals = Messages.getString(Constants.ResourceApplication, null, "AllHotDeals", null);
		category1.setCategoryName(allDeals);
		allDBCategoryWithAllDealCat.add(0, category1);

		// get the most recent threads
		// mostRecentThreadList = threadEjb.getMostRecentThread(15);

		// get the most popular threads
		trendingPopularThreadList = threadEjb.getTrendingPopularThread(8, 3);

		// sort the list by createTime
		Collections.sort(allDashBoardThreads, Collections.reverseOrder(new ThreadDashBoardComparator()));

	}

	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/10", hour = "*", persistent = false)
	public void refreshCache() {
		init();

	}

	/**
	 * @return the categoryThreadsMap
	 */
	public Map<ForumCategory, List<ThreadDashBoard>> getCategoryThreadsMap() {
		return categoryThreadsMap;
	}

	/**
	 * @param categoryThreadsMap
	 *            the categoryThreadsMap to set
	 */
	public void setCategoryThreadsMap(Map<ForumCategory, List<ThreadDashBoard>> categoryThreadsMap) {
		this.categoryThreadsMap = categoryThreadsMap;
	}

	public List<ThreadDashBoard> getDashBoards(ForumCategory category) {
		if (category != null) {
			return categoryThreadsMap.get(category);
		} else {
			return null;
		}

	}

	public int getCategoryId(ForumCategory category) {
		if (category == null) {
			return 0;
		}
		List<ThreadDashBoard> threadDashBoards = getDashBoards(category);
		if (threadDashBoards != null && !threadDashBoards.isEmpty()) {
			return threadDashBoards.get(0).getCategoryId();
		} else {
			return 0;
		}
	}

	/**
	 * @return the allCategoryArray
	 */
	public ForumCategory[] getAllCategoryArray() {
		return allCategoryArray;
	}

	/**
	 * @param allCategoryArray
	 *            the allCategoryArray to set
	 */
	public void setAllCategoryArray(ForumCategory[] allCategoryArray) {
		this.allCategoryArray = allCategoryArray;
	}

	public List<ThreadDashBoard> getAllDashBoardThreads() {
		return allDashBoardThreads;
	}

	public void setAllDashBoardThreads(List<ThreadDashBoard> allDashBoardThreads) {
		this.allDashBoardThreads = allDashBoardThreads;
	}

	/*
	 * public List<ForumThread> getMostRecentThreadList() { return mostRecentThreadList; }
	 * 
	 * public void setMostRecentThreadList(List<ForumThread> mostRecentThreadList) { this.mostRecentThreadList = mostRecentThreadList; }
	 */
	public List<ForumThread> getTrendingPopularThreadList() {
		return trendingPopularThreadList;
	}

	public void setTrendingPopularThreadList(List<ForumThread> trendingPopularThreadList) {
		this.trendingPopularThreadList = trendingPopularThreadList;
	}

	public List<ForumCategory> getAllDBCategoryWithAllDealCat() {
		return allDBCategoryWithAllDealCat;
	}

	public void setAllDBCategoryWithAllDealCat(List<ForumCategory> allDBCategoryWithAllDealCat) {
		this.allDBCategoryWithAllDealCat = allDBCategoryWithAllDealCat;
	}

}
