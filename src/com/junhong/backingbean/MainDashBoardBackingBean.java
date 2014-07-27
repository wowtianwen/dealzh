package com.junhong.backingbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.junhong.forum.common.Constants;
import com.junhong.forum.common.DisplayTypeMainPage;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ThreadDashBoard;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.forum.service.ThreadDashBoardCacheService;
import com.junhong.news.ejb.NewsCategoryEjb;
import com.junhong.news.ejb.NewsDashBoardCacheService;
import com.junhong.news.entity.NewsCategory;
import com.junhong.news.entity.NewsDashBoard;

@Named
@ViewScoped
public class MainDashBoardBackingBean {

	// picture news on the main dashboard
	private List<NewsDashBoard>					pictureNewsDB;

	// hotNews, user manually choose across all news Category;
	private Map<String, List<NewsDashBoard>>	mixedNewsDBMap					= new HashMap<String, List<NewsDashBoard>>();
	private List<String>						mixedNewsCategoryNameList		= new ArrayList<String>();

	// onMainDashBoardFlag on the newsCategory. it will be displayed on the main
	// Dashboard.
	// it is controlled by a flag on the NewsCategory
	private Map<String, List<NewsDashBoard>>	regularNewsDBMap				= new HashMap<String, List<NewsDashBoard>>();
	private List<String>						regularNewsCategoryNameList		= new ArrayList<String>();

	// similar as hot News
	private List<String>						mixedThreadCategoryNameList		= new ArrayList<String>();

	// similar to regularNews
	private List<String>						regularThreadCategoryNameList	= new ArrayList<String>();

	private List<NewsDashBoard>					allDashBoardNewsDB				= new ArrayList<NewsDashBoard>();
	private List<ThreadDashBoard>				dashBoardThreadsDB				= new ArrayList<ThreadDashBoard>();
	@EJB
	private NewsDashBoardCacheService			newsDashBoardCacheService;

	@EJB
	private ThreadDashBoardCacheService			threadDashBoardCacheService;
	@EJB
	private NewsCategoryEjb						newsCategoryEjb;
	@EJB
	private CategoryServiceSingleton			forumCategoryEjb;

	@PostConstruct
	private void init() {

		this.pictureNewsDB = newsDashBoardCacheService.getPictureNewsList();
		allDashBoardNewsDB = newsDashBoardCacheService.getAllNewsDashBoard();

		// init regularnews / regular thread/ mix thread/ mix news
		NewsCategory currNewsCategory = new NewsCategory();
		NewsCategory tempNewsCategory = null;
		// populate regularNews and mixedNews
		List<NewsDashBoard> newsDBList = null;
		for (NewsDashBoard newsDB : allDashBoardNewsDB) {
			tempNewsCategory = newsCategoryEjb.getNewsCategoryById(newsDB.getNewsCategoryId());
			// skip category if it is displayTypeonMainIndexPae is null or none
			if (tempNewsCategory.getDisplayTypeOnMainIndexPage() == null
					|| tempNewsCategory.getDisplayTypeOnMainIndexPage().equals(DisplayTypeMainPage.NONE)) {
				continue;
			}
			if (!newsDB.isPictureNews()) {
				if (!currNewsCategory.equals(tempNewsCategory)) {
					currNewsCategory = tempNewsCategory;
					newsDBList = new ArrayList<NewsDashBoard>();
					newsDBList.add(newsDB);
					if (currNewsCategory.getDisplayTypeOnMainIndexPage().equals(DisplayTypeMainPage.REGULAR)) {
						regularNewsDBMap.put(currNewsCategory.getCategoryName(), newsDBList);
					} else if (currNewsCategory.getDisplayTypeOnMainIndexPage().equals(DisplayTypeMainPage.MIXED)) {
						mixedNewsDBMap.put(currNewsCategory.getCategoryName(), newsDBList);
					}
				} else {
					newsDBList.add(newsDB);
				}

			}

		}

		// populate regular newsCategoryName list
		regularNewsCategoryNameList.addAll(regularNewsDBMap.keySet());
		mixedNewsCategoryNameList.addAll(mixedNewsDBMap.keySet());

		// for thread dashboard
		threadDashBoardCacheService.init();
		dashBoardThreadsDB = threadDashBoardCacheService.getAllDashBoardThreads();
	}

	/**
	 * set the -999999 forumcategory to the session, so that lazyThradDataModel will featch all the threads regardless its category
	 */
	public void prepareFeatch4AllThreadDB(ForumCategory category) {
		if (category == null) {
			category = new ForumCategory();
			category.setId(Constants.CategoryId_For_All_Threads);
		}
		if (category.getId() == Constants.CategoryId_For_All_Threads) {
			dashBoardThreadsDB = threadDashBoardCacheService.getAllDashBoardThreads();
		} else {
			dashBoardThreadsDB = threadDashBoardCacheService.getDashBoards(category);
		}

	}

	/**
	 * get mixed the newsDashBoard for the given newsCategoryName
	 * 
	 * @param newsCategoryName
	 * @return
	 */
	public List<NewsDashBoard> getMixedNewsDashBoard(String newsCategoryName) {
		return mixedNewsDBMap.get(newsCategoryName);
	}

	/**
	 * get regular NewsDashBoard for the given category
	 * 
	 * @param newsCategoryName
	 * @return
	 */
	public List<NewsDashBoard> getRegularNewsDashBoard(String newsCategoryName) {
		if (!newsCategoryName.isEmpty()) {
			return regularNewsDBMap.get(newsCategoryName);
		}
		return null;
	}

	// ------------getter/setter------------------------------
	public List<NewsDashBoard> getPictureNewsDB() {
		return pictureNewsDB;
	}

	public void setPictureNewsDB(List<NewsDashBoard> pictureNewsDB) {
		this.pictureNewsDB = pictureNewsDB;
	}

	public Map<String, List<NewsDashBoard>> getMixedNewsDBMap() {
		return mixedNewsDBMap;
	}

	public void setMixedNewsDBMap(Map<String, List<NewsDashBoard>> mixedNewsDBMap) {
		this.mixedNewsDBMap = mixedNewsDBMap;
	}

	public Map<String, List<NewsDashBoard>> getRegularNewsDBMap() {
		return regularNewsDBMap;
	}

	public void setRegularNewsDBMap(Map<String, List<NewsDashBoard>> regularNewsDBMap) {
		this.regularNewsDBMap = regularNewsDBMap;
	}

	public List<NewsDashBoard> getAllDashBoardNewsDB() {
		return allDashBoardNewsDB;
	}

	public void setAllDashBoardNewsDB(List<NewsDashBoard> allDashBoardNewsDB) {
		this.allDashBoardNewsDB = allDashBoardNewsDB;
	}

	public List<String> getRegularNewsCategoryNameList() {
		return regularNewsCategoryNameList;
	}

	public void setRegularNewsCategoryNameList(List<String> regularNewsCategoryNameList) {
		this.regularNewsCategoryNameList = regularNewsCategoryNameList;
	}

	public List<String> getRegularThreadCategoryNameList() {
		return regularThreadCategoryNameList;
	}

	public void setRegularThreadCategoryNameList(List<String> regularThreadCategoryNameList) {
		this.regularThreadCategoryNameList = regularThreadCategoryNameList;
	}

	public List<String> getMixedNewsCategoryNameList() {
		return mixedNewsCategoryNameList;
	}

	public void setMixedNewsCategoryNameList(List<String> mixedNewsCategoryNameList) {
		this.mixedNewsCategoryNameList = mixedNewsCategoryNameList;
	}

	public List<String> getMixedThreadCategoryNameList() {
		return mixedThreadCategoryNameList;
	}

	public void setMixedThreadCategoryNameList(List<String> mixedThreadCategoryNameList) {
		this.mixedThreadCategoryNameList = mixedThreadCategoryNameList;
	}

	public List<ThreadDashBoard> getDashBoardThreadsDB() {
		return dashBoardThreadsDB;
	}

	public void setDashBoardThreadsDB(List<ThreadDashBoard> dashBoardThreadsDB) {
		this.dashBoardThreadsDB = dashBoardThreadsDB;
	}

}
