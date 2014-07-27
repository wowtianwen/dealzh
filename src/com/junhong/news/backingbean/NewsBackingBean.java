package com.junhong.news.backingbean;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.stats.CacheKey;
import com.junhong.forum.stats.CacheType;
import com.junhong.forum.stats.HitCache;
import com.junhong.forum.stats.RatingCache;
import com.junhong.news.datamodel.LazyNewsDataModel;
import com.junhong.news.ejb.NewsCategoryEjb;
import com.junhong.news.ejb.NewsEjb;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
public class NewsBackingBean extends AbstractBacking {

	// this will determine if it is in view/edit mode
	private boolean				editMode				= false;

	@Inject
	private LazyNewsDataModel	lazyNewsDataModel;
	@Inject
	News						news					= new News();
	@Inject
	private Login				login;
	@EJB
	NewsEjb						newsEjb;
	@EJB
	NewsCategoryEjb				categoryEjb;
	@EJB
	private HitCache			hitCache;

	@EJB
	private RatingCache			ratingCache;

	private NewsCategory		belongingNewsCategory;

	// user's current position event
	@Inject
	private Event<NewsCategory>	currentCategoryEvent;
	@Inject
	private Event<News>			currentNewsEvent;
	private User				currUser;
	private boolean				ownerForCurrCategory	= false;
	private List<News>			mostRecentNews;
	private boolean				asPictureNews			= false;

	@PostConstruct
	public void initialize() {
		int id = -1;
		Object cat_news_id = this.getSessionMap().get(Constants.CATEGORY__NEWS_ID);
		if (cat_news_id != null) {
			id = Integer.parseInt((String) cat_news_id);
			Object belongCategory = this.getSessionMap().get(Constants.BelonginNewsgCategory);
			if (null != belongCategory && ((NewsCategory) belongCategory).getId() == id) {
				belongingNewsCategory = (NewsCategory) belongCategory;
			} else {
				belongingNewsCategory = categoryEjb.getNewsCategoryById(id);
				this.getSessionMap().put(Constants.BelonginNewsgCategory, belongingNewsCategory);
			}
		} else {
			belongingNewsCategory = (NewsCategory) this.getSessionMap().get(Constants.BelonginNewsgCategory);

		}

		currUser = getCurrentUser();

		// populate isOwnerForCurrCategory
		ownerForCurrCategory = calculateOwnerForCurrCategory(currUser, belongingNewsCategory);

	}

	public boolean calculateOwnerForCurrCategory(User user, NewsCategory category) {

		boolean result = false;
		if (user != null && category != null && user.equals(category.getCategoryOwner())) {
			result = true;
		}
		return result;

	}

	/* -------------business logic----------------- */
	/**
	 * @param News
	 * @return
	 */
	public String createNews() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (!login.checkIfUserActive(currUser.getUserId())) {
			return null;
		}
		this.news.setContent("<p>" + this.news.getContent() + "</p>");
		this.news.setNewsCategory(belongingNewsCategory);
		this.news.setOwner(currUser);
		// set Thumb Pic path
		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.news);
		if (hasError) {
			return null;
		} else {
			return Constants.NavSuccess;
		}

	}

	/**
	 * prerender view
	 */
	public void calculateNavMap() {
		if (belongingNewsCategory != null) {
			// update the navigation map
			currentCategoryEvent.fire(belongingNewsCategory);
		}
	}

	/**
	 * pre renderview for viewing News
	 */
	public void loadNews() {

		int newsId = 0;
		try {
			Object newsIdObj = this.getSessionMap().get("newsId");
			newsId = Integer.parseInt((String) newsIdObj);
		} catch (NumberFormatException e) {
			setBizErrorNSkipToResp("INVALIDNEWS");
			news = null;
			return;
		}
		news = newsEjb.getNewsById(newsId);
		if (news == null) {
			setBizErrorNSkipToResp("INVALIDNEWS");
			news = null;
			return;

		} else {
			belongingNewsCategory = news.getNewsCategory();
			this.getSessionMap().put(Constants.BelonginNewsgCategory, belongingNewsCategory);
			hitCache.incrementAndGet(new CacheKey(news.getId(), CacheType.NEWS));
			currentNewsEvent.fire(news);
		}

	}

	/**
	 * 
	 * @param rating
	 *            values [1,-1,0] Score: number of votes
	 */
	public void handleVotes(int vote) {
		int newVote = ratingCache.incrementAndGet(new CacheKey(news.getId(), CacheType.NEWS), 0);
		this.getRequestMap().put("score", newVote);
	}

	public News refreshNews(News news) {
		return newsEjb.getNewsById(news.getId());
	}

	public String updateNews() {

		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, news);
		news = refreshNews(news);
		this.enableEditMode(false);
		return Constants.NavNull;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		News news = (News) entity;

		if (action.equals(Constants.Action_UPDATE)) {
			newsEjb.updateNews(news);

		}
		if (action.equals(Constants.Action_CREATE)) {
			newsEjb.createNews(news);

			categoryEjb.updateNumOfNews(belongingNewsCategory, +1);
		}
		if (action.equals(Constants.Action_DELETE)) {
			newsEjb.deleteNews(news);

			categoryEjb.updateNumOfNews(belongingNewsCategory, -1);
		}

		if (action.equals(Constants.Action_DASHBOARD)) {

			newsEjb.dashBoardNewsIndex(news, this.asPictureNews);
			// reset the aspictureNews flag;
			this.asPictureNews = false;
		}
		if (action.equals(Constants.Action_UNDASHBOARD)) {

			newsEjb.unDashBoardNewsIndex(news);
		}

	}

	public AtomicInteger getVote(int id) {
		return ratingCache.getVote(new CacheKey(id, CacheType.NEWS));
	}

	public void topNews(News news, boolean top) {

		news.setTopped(top);
		news.setToppedTime(new Date());
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, news);
	}

	/**
	 * @param news
	 * @return
	 */
	public void deleteNews(News news) {

		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, news);
	}

	/**
	 * get the loaded thread
	 * 
	 * @return
	 */
	public News getLoadedNews() {
		News loadedNews = newsEjb.getLoadedNewsById(news.getId());
		return loadedNews;
	}

	public void lockNews(boolean status) {
		this.news = newsEjb.getNewsById(this.news.getId());
		this.news.setLocked(status);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, news);
	}

	public void enableEditMode(boolean editMode) {
		if (editMode) {
			news = this.refreshNews(news);
		}
		this.setEditMode(editMode);
	}

	/**
	 * put thread on the main dashboard
	 * 
	 * @param news
	 */
	public void dashBoardNewsIndex(News news, boolean asPictureNews) {
		FacesMessage message = null;
		if (news.isLocked()) {
			message = Messages.getMessage("", "LOCKED_News_NOT_ALLOWED_ONDASHBOARD", null, false);
		} else {
			this.asPictureNews = asPictureNews;
			processBusinessWithAuthorizationCheck(Constants.Action_DASHBOARD, news);
			message = Messages.getMessage("", "SUCCEED", null, false);

		}
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * take the news outof the News index page
	 * 
	 * @param news
	 */
	public void unDashBoardNewsIndex(News news) {
		processBusinessWithAuthorizationCheck(Constants.Action_UNDASHBOARD, news);
		FacesMessage message = Messages.getMessage("", "SUCCEED", null, false);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * get the most recent news
	 * 
	 * @param size
	 * @return
	 */
	// deprecated, used the cache one instead
	/*
	 * public List<News> getMostRecentNews(int size) { if (mostRecentNews ==
	 * null) { mostRecentNews = newsEjb.getMostRecentNews(size); } return
	 * mostRecentNews; }
	 */
	public List<News> getMostRecentNews(int size, NewsCategory newsCategory) {
		if (mostRecentNews == null) {
			mostRecentNews = newsEjb.getMostRecentNews(size, newsCategory);
		}
		return mostRecentNews;
	}

	public NewsBackingBean() {
		// TODO Auto-generated constructor stub
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public LazyNewsDataModel getLazyNewsDataModel() {
		return lazyNewsDataModel;
	}

	public void setLazyNewsDataModel(LazyNewsDataModel lazyNewsDataModel) {
		this.lazyNewsDataModel = lazyNewsDataModel;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public NewsCategoryEjb getCategoryEjb() {
		return categoryEjb;
	}

	public void setCategoryEjb(NewsCategoryEjb categoryEjb) {
		this.categoryEjb = categoryEjb;
	}

	public HitCache getHitCache() {
		return hitCache;
	}

	public void setHitCache(HitCache hitCache) {
		this.hitCache = hitCache;
	}

	public RatingCache getRatingCache() {
		return ratingCache;
	}

	public void setRatingCache(RatingCache ratingCache) {
		this.ratingCache = ratingCache;
	}

	public Event<NewsCategory> getCurrentCategoryEvent() {
		return currentCategoryEvent;
	}

	public void setCurrentCategoryEvent(Event<NewsCategory> currentCategoryEvent) {
		this.currentCategoryEvent = currentCategoryEvent;
	}

	public Event<News> getCurrentNewsEvent() {
		return currentNewsEvent;
	}

	public void setCurrentNewsEvent(Event<News> currentNewsEvent) {
		this.currentNewsEvent = currentNewsEvent;
	}

	public User getCurrUser() {
		return currUser;
	}

	public void setCurrUser(User currUser) {
		this.currUser = currUser;
	}

	public boolean isOwnerForCurrCategory() {
		return ownerForCurrCategory;
	}

	public void setOwnerForCurrCategory(boolean ownerForCurrCategory) {
		this.ownerForCurrCategory = ownerForCurrCategory;
	}

	public NewsCategory getBelongingNewsCategory() {
		return belongingNewsCategory;
	}

	public void setBelongingNewsCategory(NewsCategory belongingNewsCategory) {
		this.belongingNewsCategory = belongingNewsCategory;
	}

	public List<News> getMostRecentNews() {
		return mostRecentNews;
	}

	public void setMostRecentNews(List<News> mostRecentNews) {
		this.mostRecentNews = mostRecentNews;
	}

}
