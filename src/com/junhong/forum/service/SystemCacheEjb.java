package com.junhong.forum.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.Store;

@Singleton
@Named
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@Lock(LockType.READ)
public class SystemCacheEjb {
	@Inject
	StoreEjb										storeEjb;
	@Inject
	ThreadEjb										threadEjb;
	@Inject
	CategoryServiceSingleton						categorySingleEjb;
	private List<Store>								popularStores;
	private List<ForumThread>						announceThreads						= new ArrayList<ForumThread>();
	private Map<ForumCategory, List<ForumThread>>	announceThreadsPerCategory			= new HashMap<ForumCategory, List<ForumThread>>();
	private List<ForumCategory>						allCategories;
	private List<ForumCategory>						allCategoriesButGlobalAnnouncement	= new ArrayList<ForumCategory>();
	private List<ForumCategory>						threadCategoryList					= new ArrayList<ForumCategory>();

	@PostConstruct
	public void init() {
		popularStores = storeEjb.getAllPopularStores();
		announceThreads.clear();
		announceThreadsPerCategory.clear();
		announceThreads = threadEjb.getAllAnnounceThreads();
		populateAnnounceThreadsPerCategory(announceThreads);
		populateAllCategories();
		// populate thread's parent category
		initCategoryList();

	}

	/**
	 * init category list for the catgegory list on the indexthreadforAll page
	 */
	public void initCategoryList() {
		threadCategoryList.addAll(getAllCategoriesButGlobalAnnouncement());
		// add All deals category
		ForumCategory category = new ForumCategory();
		category.setId(Constants.CategoryId_For_All_Threads);
		String allDeals = Messages.getString(Constants.ResourceApplication, null, "AllHotDeals", null);
		category.setCategoryName(allDeals);
		threadCategoryList.add(0, category);
	}

	/**
	 * populate All Categories.
	 */
	@Lock(LockType.READ)
	private void populateAllCategories() {
		this.allCategories = categorySingleEjb.findForumCategories();
		// populate non-global announcement categories.
		allCategoriesButGlobalAnnouncement.clear();
		for (ForumCategory category : allCategories) {
			if (!category.isGlobalAnnouncement()) {
				allCategoriesButGlobalAnnouncement.add(category);
			}
		}
	}

	/**
	 * popolate announce thread for each category
	 * 
	 * @param announceThreads
	 */
	public void populateAnnounceThreadsPerCategory(List<ForumThread> announceThreads) {
		for (ForumThread ft : announceThreads) {
			if (!announceThreadsPerCategory.keySet().contains(ft.getCategory())) {
				List<ForumThread> announceThreadList = new ArrayList<ForumThread>();
				announceThreadList.add(ft);
				announceThreadsPerCategory.put(ft.getCategory(), announceThreadList);
			} else {
				announceThreadsPerCategory.get(ft.getCategory()).add(ft);
			}
		}

	}

	public List<Store> getPopularStores() {
		return popularStores;
	}

	public void setPopularStores(List<Store> popularStores) {
		this.popularStores = popularStores;
	}

	public List<ForumThread> getAnnounceThreads() {
		return announceThreads;
	}

	public void setAnnounceThreads(List<ForumThread> announceThreads) {
		this.announceThreads = announceThreads;
	}

	public Map<ForumCategory, List<ForumThread>> getAnnounceThreadsPerCategory() {
		return announceThreadsPerCategory;
	}

	public void setAnnounceThreadsPerCategory(Map<ForumCategory, List<ForumThread>> announceThreadsPerCategory) {
		this.announceThreadsPerCategory = announceThreadsPerCategory;
	}

	public List<ForumCategory> getAllCategories() {
		return allCategories;
	}

	public void setAllCategories(List<ForumCategory> allCategories) {
		this.allCategories = allCategories;
	}

	public List<ForumCategory> getAllCategoriesButGlobalAnnouncement() {
		return allCategoriesButGlobalAnnouncement;
	}

	public void setAllCategoriesButGlobalAnnouncement(List<ForumCategory> allCategoriesButGlobalAnnouncement) {
		this.allCategoriesButGlobalAnnouncement = allCategoriesButGlobalAnnouncement;
	}

	public List<ForumCategory> getThreadCategoryList() {
		return threadCategoryList;
	}

	public void setThreadCategoryList(List<ForumCategory> threadCategoryList) {
		this.threadCategoryList = threadCategoryList;
	}

}