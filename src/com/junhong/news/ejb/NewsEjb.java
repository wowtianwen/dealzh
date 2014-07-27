package com.junhong.news.ejb;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.OwnerCheck;
import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.GenericCRUDService;
import com.junhong.news.dao.NewsDAO;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;
import com.junhong.news.entity.NewsDashBoard;

/**
 */
@Stateless
@LocalBean
public class NewsEjb extends GenericCRUDService<News> {

	@Inject
	private NewsDAO newsDao;
	@Inject
	private News news;
	@Inject
	private Logger logger;
	@EJB
	private GenericCRUDService<NewsDashBoard> genericCRUDService;

	/**
	 * Default constructor.
	 */
	public NewsEjb() {
		// TODO Auto-generated constructor stub
	}

	public News getNewsById(int id) {
		return newsDao.findById(News.class, id);
	}

	/**
	 * @param id
	 * @return
	 */
	public News getLoadedNewsById(int id) {
		News news = newsDao.findById(News.class, id);
		news.getComments().size();
		return news;
	}

	/**
	 * @param category
	 * @return
	 */
	public List<News> getNewsListByCategory(NewsCategory category) {
		return newsDao.getNewsByCategory(category);
	}

	/**
	 * @param category
	 * @return
	 */
	public int getNewsCountByCategory(NewsCategory category, boolean isUserAccessableToLockedThreads) {
		return (int) newsDao.getTotalCount(category, isUserAccessableToLockedThreads);
	}

	/**
	 * @param category
	 * @return
	 */
	public int getNewsCountByOwner(User owner, boolean isUserAccessableToLockedThreads) {
		return (int) newsDao.getTotalCount(owner, isUserAccessableToLockedThreads);
	}

	/**
	 * @param newsCategory
	 * @param start
	 * @param size
	 * @return
	 */
	public List<News> getNewsListByCategory(NewsCategory newsCategory, int start, int size, List<Integer> excludeId, boolean isUserAccessableToLockedNews) {
		List<News> NewsList = null;
		NewsList = newsDao.getNewsByCategory(newsCategory, start, size, excludeId, isUserAccessableToLockedNews, false);
		return NewsList;
	}

	public List<News> getMostRecentNews(int size) {
		String hqlMostRecent = "select news from News news  where news.onDashBoard=false order by news.createTime desc ";
		return newsDao.findByHQL(hqlMostRecent, News.class, 0, size);
	}

	/**
	 * get the most recent news for the given category
	 * 
	 * @param size
	 * @return
	 */
	public List<News> getMostRecentNews(int size, NewsCategory newsCategory) {
		return newsDao.getNewsByCategory(newsCategory, 0, size, null, false, true);
	}

	/**
	 * get all the threads for the given User
	 * 
	 * @param user
	 * @param start
	 * @param size
	 * @param excludeshreadsId
	 * @param isUserAccessableToLockedThreads
	 * @return
	 */
	public List<News> getNewsList(User user, int start, int size) {
		List<News> NewsList = null;
		NewsList = newsDao.getNewsByOwner(user, start, size, false);
		return NewsList;
	}

	/**
	 * use pessimistic write locking to lock the row so that numofComment can be
	 * update sequencially while it still can be read
	 * 
	 * @param news
	 */
	@Asynchronous
	public void updateNumOfComments(News news, int delta) {

		news = findByIdWithPessimisticWrite(news.getId());

		news.setNumOfComments(news.getNumOfComments() + delta);
		newsDao.update(news);

	}

	public void updateVotes(int id, int votes) {
		newsDao.updateVotes(id, votes);
	}

	/**
	 * create a new Thread
	 * 
	 * @param News
	 */

	@Role.List({ @Role(RoleType.NEWSPOSTER), @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createNews(News News) throws AuthorizationFailException {
		newsDao.create(News);
	}

	/**
	 * update Thread
	 * 
	 * @param News
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@OwnerCheck
	@Interceptors(AuthorizationInterceptor.class)
	public void updateNews(News News) throws AuthorizationFailException {
		newsDao.update(News);
	}

	public void updateNewsWithAuthorization(News News) {
		newsDao.update(News);
	}

	/**
	 * delete thread
	 * 
	 * @param News
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void deleteNews(News News) throws AuthorizationFailException {
		newsDao.delete(News);
	}

	public void refreshNews(News News) {
		newsDao.refresh(News);
	}

	public void updateNumOfView(int id, int numOfHit) {
		newsDao.updateNumOfView(id, numOfHit);
	}

	/**
	 * @param thread
	 */
	public News findByIdWithPessimisticWrite(int id) {

		return newsDao.findByIdWithPessimisticWrite(News.class, id);

	}

	public List<News> getToppedNewsList(NewsCategory category) {

		return newsDao.getToppedNewsList(category);

	}

	/**
	 * put thread on the main dashboard
	 * 
	 * @param news
	 */
	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void dashBoardNewsIndex(News news, boolean asPictureNews) throws AuthorizationFailException {

		news = this.findByIdWithPessimisticWrite(news.getId());
		news.setOnDashBoard(true);
		this.update(news);
		// insert it into the dashboard table
		NewsDashBoard newsDB = new NewsDashBoard();
		newsDB.setNewsCategoryId(news.getNewsCategory().getId());
		newsDB.setNewsId(news.getId());
		newsDB.setNewsSubject(news.getSubject());
		newsDB.setNewsDescription(news.getDescription());
		newsDB.setPicturePathURL(news.getThumbPicURL());
		newsDB.setPictureNews(asPictureNews);
		genericCRUDService.create(newsDB);

	}

	@Role.List({ @Role(RoleType.CATEGORY_OWNER), @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void unDashBoardNewsIndex(News news) throws AuthorizationFailException {

		news = this.findByIdWithPessimisticWrite(news.getId());
		news.setOnDashBoard(false);
		this.update(news);

		// remove it from Newsdashboard table
		String query = "select ndb from NewsDashBoard ndb where  ndb.newsId=?1";
		NewsDashBoard result = genericCRUDService.findByHQLSingleResult(query, NewsDashBoard.class, news.getId());
		if (result != null) {
			genericCRUDService.delete(result);
		}

	}
}
