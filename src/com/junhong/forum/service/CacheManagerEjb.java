package com.junhong.forum.service;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.junhong.forum.stats.RatingCache;
import com.junhong.news.ejb.NewsCacheEjb;
import com.junhong.news.ejb.NewsCategoryEjb;
import com.junhong.news.ejb.NewsDashBoardCacheService;

/**
 * manage global/all cache.
 * 
 * @author zhanjung
 * 
 */
@Stateless
@LocalBean
public class CacheManagerEjb {
	@EJB
	private CategoryServiceSingleton	categoryService;
	@EJB
	private ThreadDashBoardCacheService	threadDBService;
	@EJB
	private ThreadCacheEjb				threadCacheEjb;
	@EJB
	private NewsCacheEjb				newsCacheEjb;
	@EJB
	private NewsCategoryEjb				newsCategoryEjb;
	@EJB
	private NewsDashBoardCacheService	newsDBCacheService;
	@EJB
	private RatingCache					ratingCache;
	@EJB
	private SystemCacheEjb				systemCacheEjb;

	public void flushAll() {
		categoryService.init();
		threadDBService.refreshCache();
		newsCacheEjb.populateRecentOverAllNews();
		newsCacheEjb.populateRecentNews();
		newsCategoryEjb.init();
		newsDBCacheService.refreshCache();
		threadCacheEjb.populateRecentPopularThreads();
		ratingCache.syncWithDb();
		systemCacheEjb.init();

	}

}
