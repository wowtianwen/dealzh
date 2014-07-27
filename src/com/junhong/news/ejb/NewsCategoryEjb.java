package com.junhong.news.ejb;

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
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.news.dao.NewsCategoryDAO;
import com.junhong.news.entity.NewsCategory;

/**
 * Session Bean implementation class NewsCategoryEjb
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Lock(LockType.READ)
public class NewsCategoryEjb {
	@Inject
	private Logger logger;

	@Inject
	private NewsCategoryDAO newsCategoryDao;
	private List<NewsCategory> topCategoryList = new ArrayList<NewsCategory>();
	private List<NewsCategory> flatAllCategoryList;

	// cache to store the numofNews,
	// THREAD SAFE
	private Map<String, NewsCategory> category_NewsMap = new ConcurrentHashMap<String, NewsCategory>();

	/**
	 * Default constructor.
	 */
	public NewsCategoryEjb() {
	}

	@PostConstruct
	public void init() {
		logger.info("Start ------ " + "fetch all the News categories ");
		flatAllCategoryList = newsCategoryDao.findAll(NewsCategory.class);

		topCategoryList.clear();
		populateTopCategories();

		category_NewsMap.clear();
		calculateNewsParentCategory();

		logger.info("End ------ " + "fetch all the News categories ");

	}

	/**
	 * get the given list of newsCategories for the given id fromt the dataBase
	 * instead of the cache
	 * 
	 * @param newsCategoryIds
	 * @return
	 */
	public List<NewsCategory> getNewsCategories(List<Integer> newsCategoryIds) {

		return newsCategoryDao.getNewsCategories(newsCategoryIds);

	}

	/**
	 * populate top categories from the flatAllCategoryList
	 */
	public void populateTopCategories() {
		for (NewsCategory fc : flatAllCategoryList) {
			if (null == fc.getParentCategory()) {
				topCategoryList.add(fc);
			}
		}

	}

	/**
	 * get the list of lowest level categories.
	 */
	private void calculateNewsParentCategory() {

		for (NewsCategory fc : flatAllCategoryList) {
			if (null == fc.getChildrenCategory() || fc.getChildrenCategory().size() == 0) {
				category_NewsMap.put(fc.getCategoryName(), fc);
			}
		}

	}

	@PreDestroy
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/10", hour = "*",persistent = false)
	public void updateCatetoryNumberOfNews() {
		newsCategoryDao.updateNumberOfNews(category_NewsMap.values());
	}

	@Lock(LockType.READ)
	@Asynchronous
	public void updateNumOfNews(NewsCategory category, int delta) {

		category.setNumOfNews(category.getNumOfNews() + delta);
	}

	@Lock(LockType.READ)
	public List<NewsCategory> getAllFlatCategories() {

		return flatAllCategoryList;
	}

	@Lock(LockType.READ)
	public List<NewsCategory> findTopCategories(int start, int size) {
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
	public List<NewsCategory> getAllTopCategories() {
		return topCategoryList;
	}

	@Lock(LockType.READ)
	public List<NewsCategory> findSubCategories(int categoryId, int start, int size) {
		//$FALL-THROUGH$
		NewsCategory currCategory = getNewsCategoryById(categoryId);
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
	public List<NewsCategory> findAllSubCategories(int categoryId) {
		//$FALL-THROUGH$
		NewsCategory currCategory = getNewsCategoryById(categoryId);
		if (currCategory != null) {
			return currCategory.getChildrenCategory();
		} else {
			return null;
		}
	}

	@Lock(LockType.READ)
	public NewsCategory getNewsCategoryById(long newsCategoryId) {
		for (NewsCategory fc : flatAllCategoryList) {
			if (newsCategoryId == fc.getId()) {
				return fc;
			}
		}
		return null;
	}

	@Lock(LockType.READ)
	public NewsCategory refreshNewsCategory(NewsCategory newsCategory) {
		return newsCategoryDao.findById(NewsCategory.class, newsCategory.getId());
	}

	@Lock(LockType.READ)
	public boolean isCategoryExists(NewsCategory category) {

		if (category.getId() == -1) {
			return flatAllCategoryList.contains(category);
		} else {
			int count = 0;
			// check for new category
			if (category.getId() == 0) {
				for (NewsCategory fc : flatAllCategoryList) {
					if (fc.equals(category)) {
						count++;
					}
				}
			}
			// check for the update of category
			else {
				for (NewsCategory fc : flatAllCategoryList) {
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
		NewsCategory currCategory = getNewsCategoryById(parentCategoryId);
		if (null != currCategory) {
			return currCategory.getChildrenCategory().size();
		}
		return 0;
	}

	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void createNewsCategory(NewsCategory newsCategory) throws AuthorizationFailException {
		this.create(newsCategory);
	}

	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void updateNewsCategory(NewsCategory newsCategory) throws AuthorizationFailException {
		this.update(newsCategory);
	}

	@Lock(LockType.WRITE)
	@Role(RoleType.SYSADMIN)
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteNewsCategory(NewsCategory newsCategory) throws AuthorizationFailException {
		this.delete(newsCategory);
	}

	public void create(NewsCategory category) {
		newsCategoryDao.create(category);
	}

	public void update(NewsCategory category) {
		newsCategoryDao.update(category);
	}

	public void delete(NewsCategory category) {
		newsCategoryDao.delete(category);
	}

	public Map<String, NewsCategory> getCategory_NewsMap() {
		return category_NewsMap;
	}

}
