package com.junhong.news.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

import com.junhong.forum.common.DisplayType;
import com.junhong.forum.service.GenericCRUDService;
import com.junhong.news.entity.NewsCategory;
import com.junhong.news.entity.NewsDashBoard;
import com.junhong.util.NewsDashBoardComparator;

/**
 * Singleton Bean implementation class DashBoardService
 */

@Named
@ApplicationScoped
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class NewsDashBoardCacheService {

	// non picture news, saved in the tab on the index page
	private Map<String, List<NewsDashBoard>>	nonPicTabNewsCategoryNewsMap		= new ConcurrentHashMap<String, List<NewsDashBoard>>();

	// non picture news, displayed in the regular page section
	private Map<String, List<NewsDashBoard>>	nonPicRegularNewsCategoryNewsMap	= new ConcurrentHashMap<String, List<NewsDashBoard>>();

	// picture news list
	private List<NewsDashBoard>					pictureNewsList						= new ArrayList<NewsDashBoard>();

	// all newsdashboard
	private List<NewsDashBoard>					allNewsDashBoard					= new ArrayList<NewsDashBoard>();
	@EJB
	private GenericCRUDService<NewsDashBoard>	genericCRUDService;
	@EJB
	private NewsCategoryEjb						newsCategoryEjb;
	// tab newsCategoryName list
	private List<String>						tabNewsCategoryNameRegList			= new ArrayList<String>();

	// regular newsCategoryName list
	private List<String>						regularNewsCategoryNameRegList		= new ArrayList<String>();

	/**
	 * Default constructor.
	 */
	public NewsDashBoardCacheService() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void init() {
		pictureNewsList.clear();
		tabNewsCategoryNameRegList.clear();
		nonPicTabNewsCategoryNewsMap.clear();
		nonPicRegularNewsCategoryNewsMap.clear();
		allNewsDashBoard.clear();
		regularNewsCategoryNameRegList.clear();

		// allnewsDashBoard should have a list ordered by the newsCategory Name
		allNewsDashBoard = genericCRUDService.findAll(NewsDashBoard.class);
		int categoryId = -999999;
		int tempCategoryId = 0;
		List<NewsDashBoard> newsDashbaords = null;
		String categoryName = null;
		NewsCategory currentNewsCategory;
		for (NewsDashBoard newsDashBoard : allNewsDashBoard) {
			if (newsDashBoard.isPictureNews()) {
				pictureNewsList.add(newsDashBoard);
			} else {
				// check the CategoryID is changed
				tempCategoryId = newsDashBoard.getNewsCategoryId();

				// every time, newscategory is encountered.
				if (categoryId != tempCategoryId) {
					categoryId = tempCategoryId;
					newsDashbaords = new ArrayList<NewsDashBoard>();

					currentNewsCategory = newsCategoryEjb.getNewsCategoryById(tempCategoryId);
					categoryName = currentNewsCategory.getCategoryName();
					if (currentNewsCategory.getDisplayType().equals(DisplayType.TAB)) {
						nonPicTabNewsCategoryNewsMap.put(categoryName, newsDashbaords);
					}

					else {
						nonPicRegularNewsCategoryNewsMap.put(categoryName, newsDashbaords);
					}
					newsDashbaords.add(newsDashBoard);
				} else {
					newsDashbaords.add(newsDashBoard);
				}

			}
		}

		// sort pictureNewsList by the createtime desc

		Collections.sort(pictureNewsList, new NewsDashBoardComparator());

		// initialize newsCategoryNameRegSet
		tabNewsCategoryNameRegList.addAll(nonPicTabNewsCategoryNewsMap.keySet());
		regularNewsCategoryNameRegList.addAll(nonPicRegularNewsCategoryNewsMap.keySet());

	}

	/**
	 * refresh the cache every certain period
	 */
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/10", hour = "*", persistent = false)
	public void refreshCache() {
		init();

	}

	public List<NewsDashBoard> getNonPicTabNewsDB(String newsCategoryName) {
		return nonPicTabNewsCategoryNewsMap.get(newsCategoryName);
	}

	public List<NewsDashBoard> getNonPicRegularNewsDB(String newsCategoryName) {
		return nonPicRegularNewsCategoryNewsMap.get(newsCategoryName);

	}

	// getter/setters

	public List<NewsDashBoard> getAllNewsDashBoard() {
		return allNewsDashBoard;
	}

	public void setAllNewsDashBoard(List<NewsDashBoard> allNewsDashBoard) {
		this.allNewsDashBoard = allNewsDashBoard;
	}

	public GenericCRUDService<NewsDashBoard> getGenericCRUDService() {
		return genericCRUDService;
	}

	public void setGenericCRUDService(GenericCRUDService<NewsDashBoard> genericCRUDService) {
		this.genericCRUDService = genericCRUDService;
	}

	public List<NewsDashBoard> getPictureNewsList() {
		return pictureNewsList;
	}

	public void setPictureNewsList(List<NewsDashBoard> pictureNewsList) {
		this.pictureNewsList = pictureNewsList;
	}

	public NewsCategoryEjb getNewsCategoryEjb() {
		return newsCategoryEjb;
	}

	public void setNewsCategoryEjb(NewsCategoryEjb newsCategoryEjb) {
		this.newsCategoryEjb = newsCategoryEjb;
	}

	public Map<String, List<NewsDashBoard>> getNonPicTabNewsCategoryNewsMap() {
		return nonPicTabNewsCategoryNewsMap;
	}

	public void setNonPicTabNewsCategoryNewsMap(Map<String, List<NewsDashBoard>> nonPicTabNewsCategoryNewsMap) {
		this.nonPicTabNewsCategoryNewsMap = nonPicTabNewsCategoryNewsMap;
	}

	public Map<String, List<NewsDashBoard>> getNonPicRegularNewsCategoryNewsMap() {
		return nonPicRegularNewsCategoryNewsMap;
	}

	public void setNonPicRegularNewsCategoryNewsMap(Map<String, List<NewsDashBoard>> nonPicRegularNewsCategoryNewsMap) {
		this.nonPicRegularNewsCategoryNewsMap = nonPicRegularNewsCategoryNewsMap;
	}

	public List<String> getTabNewsCategoryNameRegList() {
		return tabNewsCategoryNameRegList;
	}

	public void setTabNewsCategoryNameRegList(List<String> tabNewsCategoryNameRegList) {
		this.tabNewsCategoryNameRegList = tabNewsCategoryNameRegList;
	}

	public List<String> getRegularNewsCategoryNameRegList() {
		return regularNewsCategoryNameRegList;
	}

	public void setRegularNewsCategoryNameRegList(List<String> regularNewsCategoryNameRegList) {
		this.regularNewsCategoryNameRegList = regularNewsCategoryNameRegList;
	}

}
