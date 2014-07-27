package com.junhong.news.ejb;

import java.util.ArrayList;
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
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.news.dao.NewsDAO;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;

@Singleton
@Named
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@Lock(LockType.READ)
public class NewsCacheEjb {
	@Inject
	private NewsDAO						newsDAO;

	@EJB
	NewsCategoryEjb						newsCategoryEjb;

	private Map<String, NewsCategory>	category_NewsMap	= null;
	int									size				= 15;

	private Map<String, List<News>>		recentNews			= new ConcurrentHashMap<String, List<News>>();

	private List<News>					recentOverAllNews	= new ArrayList<News>();

	/**
	 * get the most recent news for given category from the cache instead of the
	 * db
	 * 
	 * @param newsCategoryName
	 * @return
	 */
	public List<News> getRecentNews(String newsCategoryName, News excludeNews) {

		List<News> news = null;
		if (newsCategoryName != null) {
			news = recentNews.get(newsCategoryName);
		}
		if (news == null) {
			news = new ArrayList<News>();
		}
		news.remove(excludeNews);
		return news;

	}

	/**
	 * get the most recent news for given category from the cache instead of the
	 * db
	 * 
	 * @param newsCategoryName
	 * @return
	 */
	public List<News> getRecentNews(String newsCategoryName) {

		List<News> news = new ArrayList<News>();
		if (newsCategoryName != null) {
			news = recentNews.get(newsCategoryName);
		}
		return news;

	}

	/**
	 * refresh cache from db for the overall news: not each category
	 * :used in the news index page
	 */
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/10", hour = "*",persistent = false)
	public void populateRecentOverAllNews() {

		String hql = "select news from News news order by news.createTime desc";
		recentOverAllNews = newsDAO.findByHQL(hql, News.class, 0, size);

	}

	/**
	 * refresh cache from db for the each news category
	 * :used in the news index page
	 */
	@PostConstruct
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/5", hour = "*",persistent = false)
	public void populateRecentNews() {

		category_NewsMap = newsCategoryEjb.getCategory_NewsMap();
		if (category_NewsMap == null || category_NewsMap.isEmpty()) {
			newsCategoryEjb.init();
			category_NewsMap = newsCategoryEjb.getCategory_NewsMap();
		}

		List<News> newsList = new ArrayList<News>();
		for (NewsCategory newsCateogry : category_NewsMap.values()) {
			newsList = newsDAO.getNewsByCategory(newsCateogry, 0, size, null, false, false);
			recentNews.put(newsCateogry.getCategoryName(), newsList);
		}

	}

	public List<News> getRecentOverAllNews() {

		if (recentOverAllNews.isEmpty()) {
			populateRecentOverAllNews();
		}
		return recentOverAllNews;
	}

	public void setRecentOverAllNews(List<News> recentOverAllNews) {
		this.recentOverAllNews = recentOverAllNews;
	}
}