/**
 * forum
 * zhanjung
 */
package com.junhong.forum.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.dao.ForumCategoryDAO;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.exceptions.AuthorizationFailException;

/**
 * all the categories are cached. any access to category is reading from the
 * cache.
 * 
 * @author zhanjung
 * 
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Singleton
@Lock(LockType.READ)
public class CategoryServiceSingleton implements Serializable {

	private static final long			serialVersionUID		= 1L;
	/* ------------instance Variable-------------- */
	private List<ForumCategory>			topCategoryList			= new ArrayList<ForumCategory>();
	private List<ForumCategory>			flatAllCategoryList;

	// cache to store the numOfThread,
	// THREAD SAFE
	// Map<categoryName, ForumCategoy>
	private Map<String, ForumCategory>	CategoryOfThreadMap		= new ConcurrentHashMap<String, ForumCategory>();
	private List<ForumCategory>			CategoryOfThreadList	= new ArrayList<ForumCategory>();

	@Inject
	Logger								logger;

	@Inject
	ForumCategoryDAO					categoryDAO;

	/* -------------business logic----------------- */
	@PostConstruct
	public void init() {
		logger.info("Start ------ " + "fetch all the categories ");
		flatAllCategoryList = categoryDAO.findAll(ForumCategory.class);

		topCategoryList.clear();
		populateTopCategories();

		CategoryOfThreadMap.clear();
		CategoryOfThreadList.clear();
		calculateThreadParentCategory();
		logger.info("End ------ " + "fetch all the categories ");

	}

	/**
	 * populate top categories from the flatAllCategoryList
	 */
	public void populateTopCategories() {
		for (ForumCategory fc : flatAllCategoryList) {
			if (null == fc.getParentCategory()) {
				topCategoryList.add(fc);
			}
		}

	}

	@PreDestroy
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/20", hour = "*", persistent = false)
	public void updateCatetoryNumberOfThead() {
		categoryDAO.updateNumberOfThead(CategoryOfThreadMap.values());
	}

	/**
	 * get the list of lowest level categories.
	 */
	private void calculateThreadParentCategory() {

		for (ForumCategory fc : flatAllCategoryList) {
			if (null == fc.getChildrenCategory() || fc.getChildrenCategory().size() == 0) {
				CategoryOfThreadMap.put(fc.getCategoryName(), fc);
				CategoryOfThreadList.add(fc);
			}
		}

	}

	/**
	 * category is return from the single Ejb class, it should return the same
	 * one all the time
	 * 
	 * @param categoryName
	 * @param numOfThread
	 */
	@Lock(LockType.READ)
	@Asynchronous
	public void updateNumOfThread(ForumCategory category, int delta) {

		category.setNumOfThread(category.getNumOfThread() + delta);
	}

	@Lock(LockType.READ)
	public List<ForumCategory> findForumCategories() {

		return flatAllCategoryList;
	}

	@Lock(LockType.READ)
	public List<ForumCategory> findTopCategories(int start, int size) {
		// return topCategoryList.subList(start, start + size);

		int listSize = topCategoryList.size();
		if (listSize > start && listSize >= start + size) {
			return topCategoryList.subList(start, start + size);
		} else if (listSize > start && listSize < start + size) {
			return topCategoryList.subList(start, listSize);
		}
		return null;

	}

	@Lock(LockType.READ)
	public List<ForumCategory> getAllTopCategories() {
		return topCategoryList;
	}

	@Lock(LockType.READ)
	public List<ForumCategory> findSubCategories(int categoryId, int start, int size) {
		//$FALL-THROUGH$
		ForumCategory currCategory = getForumCategoryById(categoryId);
		if (null != currCategory) {
			int listSize = currCategory.getChildrenCategory().size();
			if (listSize > start && listSize >= start + size) {
				return currCategory.getChildrenCategory().subList(start, start + size);
			} else if (listSize > start && listSize < start + size) {
				return currCategory.getChildrenCategory().subList(start, listSize);

			}
		}
		return null;
	}

	@Lock(LockType.READ)
	public List<ForumCategory> findAllSubCategories(int categoryId) {
		//$FALL-THROUGH$
		ForumCategory currCategory = getForumCategoryById(categoryId);

		if (currCategory != null) {
			return currCategory.getChildrenCategory();
		} else {
			return new ArrayList<ForumCategory>();
		}
	}

	@Lock(LockType.READ)
	public ForumCategory getForumCategoryById(long forumCategoryId) {
		for (ForumCategory fc : flatAllCategoryList) {
			if (forumCategoryId == fc.getId()) {
				return fc;
			}
		}
		return null;
	}

	@Lock(LockType.READ)
	public boolean isCategoryExists(ForumCategory category) {

		if (category.getId() == -1) {
			return flatAllCategoryList.contains(category);
		} else {
			int count = 0;
			// check for new category
			if (category.getId() == 0) {
				for (ForumCategory fc : flatAllCategoryList) {
					if (fc.equals(category)) {
						count++;
					}
				}
			}
			// check for the update of category
			else {
				for (ForumCategory fc : flatAllCategoryList) {
					if (category.getId() != fc.getId() && fc.equals(category)) {
						count++;
					}
				}
			}

			if (count > 0) {
				return true;
			}

		}
		return false;
	}

	@Lock(LockType.READ)
	public int getTotalTopCategoryCount() {
		return topCategoryList == null ? 0 : topCategoryList.size();
	}

	@Lock(LockType.READ)
	public long getTotalSubcategoryCount(int parentCategoryId) {
		ForumCategory currCategory = getForumCategoryById(parentCategoryId);
		if (null != currCategory) {
			return currCategory.getChildrenCategory().size();
		}
		return 0;
	}

	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void createForumCategory(ForumCategory forumCategory) {
		categoryDAO.create(forumCategory);
		init();

	}

	/**
	 * @param forumCategory
	 */
	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void updateForumCategory(ForumCategory forumCategory) throws AuthorizationFailException {
		logger.info("category:" + forumCategory.getCategoryName() + "is going to be updated");
		categoryDAO.update(forumCategory);
		// update the Category List
		init();
	}

	/**
	 * @param forumCategory
	 */
	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteForumCategory(ForumCategory forumCategory) throws AuthorizationFailException {
		categoryDAO.delete(forumCategory);
		init();
	}

	@Lock(LockType.READ)
	public ForumCategory getGlobalAnnouncementCategory() {
		ForumCategory result = null;
		for (ForumCategory category : flatAllCategoryList) {
			if (category.isGlobalAnnouncement()) {
				result = category;
				break;
			}

		}
		return result;
	}

	/**
	 * refresh from the db
	 * 
	 * @param forumCategory
	 * @return
	 */
	public ForumCategory refreshCategory(ForumCategory forumCategory) {
		return categoryDAO.findById(ForumCategory.class, forumCategory.getId());
	}

	public Map<String, ForumCategory> getCategoryOfThreadMap() {
		return CategoryOfThreadMap;
	}

	public void setCategoryOfThreadMap(Map<String, ForumCategory> categoryOfThreadMap) {
		CategoryOfThreadMap = categoryOfThreadMap;
	}

	public List<ForumCategory> getCategoryOfThreadList() {
		return CategoryOfThreadList;
	}

	public void setCategoryOfThreadList(List<ForumCategory> categoryOfThreadList) {
		CategoryOfThreadList = categoryOfThreadList;
	}

	// SHOULD BE DELETED
	/*
	 * public int getTotalNumberOfThread(ForumCategory category) { // TODO
	 * return 0; }
	 */

	/* -------------getter/setter----------------- */

}
