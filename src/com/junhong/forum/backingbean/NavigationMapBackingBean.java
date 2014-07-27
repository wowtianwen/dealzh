/**
 * forum zhanjung
 */
package com.junhong.forum.backingbean;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */
@Named
@SessionScoped
public class NavigationMapBackingBean implements Serializable {
	private static final long	serialVersionUID			= 01l;

	/* ------------instance Variable-------------- */
	private List<ForumCategory>	navigationMap				= new ArrayList<ForumCategory>();
	ForumThread					currentThread				= null;
	// variable for News
	private List<NewsCategory>	navigationMapNewsCategory	= new ArrayList<NewsCategory>();
	News						currentNews					= null;

	/* -------------business logic----------------- */
	/**
	 * calculate the navigatin map Navigation map contains category hierachy
	 * 
	 * @param currCategoryId
	 */
	public void calculateNavitationMap(ForumCategory currCategory) {
		// navigationMap
		// String currCategoryId = (String)
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.NAVIGATION_CATEGORY_ID);
		getNavigationMap().clear();
		if (currCategory.getId() == -1) {
			return;
		}
		Deque<ForumCategory> stack = new ArrayDeque<ForumCategory>();
		stack.addFirst(currCategory);
		while (currCategory.getParentCategory() != null) {
			currCategory = currCategory.getParentCategory();
			stack.addFirst(currCategory);
		}
		while (!stack.isEmpty()) {
			getNavigationMap().add(stack.removeFirst());
		}
	}

	public void updateNavigationMap(@Observes ForumCategory currentCategory) {

		if (currentCategory != null) {
			calculateNavitationMap(currentCategory);
		}
		// set currentThread to null if the user is on the category level
		this.currentThread = null;

	}

	public void updateNavigationMap(@Observes ForumThread currentThread) {
		this.currentThread = currentThread;
		calculateNavitationMap(currentThread.getCategory());
	}

	/**
	 * clear the map so that it wont display anything on the Thread index page
	 */
	public void clearNavigationMap() {
		getNavigationMap().clear();
		this.currentThread = null;
	}

	/**
	 * populate navigationmap for News
	 * 
	 * @param currCategory
	 */
	public void calculateNavitationMapNews(NewsCategory currCategory) {
		// navigationMap
		// String currCategoryId = (String)
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.NAVIGATION_CATEGORY_ID);
		getNavigationMapNewsCategory().clear();
		if (currCategory.getId() == -1) {
			return;
		}
		Deque<NewsCategory> stack = new ArrayDeque<NewsCategory>();
		stack.addFirst(currCategory);
		while (currCategory.getParentCategory() != null) {
			currCategory = currCategory.getParentCategory();
			stack.addFirst(currCategory);
		}
		while (!stack.isEmpty()) {
			getNavigationMapNewsCategory().add(stack.removeFirst());
		}

	}

	/**
	 * populate navigationmap for News
	 * 
	 * @param currentCategory
	 */
	public void updateNavigationMapNewsCategory(@Observes NewsCategory currentCategory) {

		if (currentCategory != null) {
			calculateNavitationMapNews(currentCategory);
		}
		// set currentNews to null if the user is on the category level
		this.currentNews = null;

	}

	/**
	 * populate navigationmap for News
	 * 
	 * @param currentNews
	 */
	public void updateNavigationMapNewsCategory(@Observes News currentNews) {
		this.currentNews = currentNews;
		calculateNavitationMapNews(currentNews.getNewsCategory());
	}

	/**
	 * clear the map so that it wont display anything on the news index page
	 */
	public void clearNavigationMapNewsCategory() {
		getNavigationMapNewsCategory().clear();
		this.currentNews = null;

	}

	/* -------------getter/setter----------------- */

	public List<ForumCategory> getNavigationMap() {
		return navigationMap;
	}

	public void setNavigationMap(List<ForumCategory> navigationMap) {
		this.navigationMap = navigationMap;
	}

	/**
	 * @return the currentThread
	 */
	public ForumThread getCurrentThread() {
		return currentThread;
	}

	/**
	 * @param currentThread
	 *            the currentThread to set
	 */
	public void setCurrentThread(ForumThread currentThread) {
		this.currentThread = currentThread;
	}

	public List<NewsCategory> getNavigationMapNewsCategory() {
		return navigationMapNewsCategory;
	}

	public void setNavigationMapNewsCategory(List<NewsCategory> navigationMapNewsCategory) {
		this.navigationMapNewsCategory = navigationMapNewsCategory;
	}

	public News getCurrentNews() {
		return currentNews;
	}

	public void setCurrentNews(News currentNews) {
		this.currentNews = currentNews;
	}

}
