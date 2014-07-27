/**
 * jsf_demo
 * zhanjung
 */
package com.junhong.news.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.news.ejb.NewsEjb;
import com.junhong.news.entity.News;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */
public class LazyNewsDataModel extends LazyDataModel<News> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8730272180146226564L;

	private NewsCategory		parentCategory;
	private User				user;
	// check where it is from: MAIN, or MYPOST in the user profile page
	private String				source				= "-1";

	@EJB
	private NewsEjb				newsEjb;

	@Inject
	private Login				login;

	public LazyNewsDataModel() {
	}

	public LazyNewsDataModel(NewsCategory category) {
		this.parentCategory = category;
	}

	@Override
	public void setRowIndex(int rowIndex) {
		// not sure if this is correct, but it will stop the error : viewthread
		// page, clieck the azhang profile, it will throw the divided by Zero
		// error.
		if (this.getPageSize() == 0) {
			this.setPageSize(1);
		}
		super.setRowIndex(rowIndex);

	}

	@Override
	public List<News> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<News> result = new ArrayList<News>();
		// For MyPost page.
		if (this.source.equals(Constants.USER_TAB_MYPOST)) {
			// load the threads for the given user
			int dataSize = newsEjb.getNewsCountByOwner(user, false);
			this.setRowCount(dataSize);

			// paginate
			result = newsEjb.getNewsList(user, first, pageSize);

		}
		// for the regular thread everyone will navigate through category and
		// thread
		else {

			parentCategory = (NewsCategory) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.BelonginNewsgCategory);
			boolean isUserAccessableToLockedNews = login.isUserInNewsCategory_Owner(parentCategory) || login.isUserInSysadmin();
			// rowCount
			int dataSize = newsEjb.getNewsCountByCategory(parentCategory, isUserAccessableToLockedNews);
			this.setRowCount(dataSize);

			// paginate
			int toppedNewsSize = 0;
			List<News> toppedNewsList = new ArrayList<News>();
			List<Integer> toppedNewsIdList = new ArrayList<Integer>();

			toppedNewsList = newsEjb.getToppedNewsList(parentCategory);
			toppedNewsSize = toppedNewsList.size();
			for (News ft : toppedNewsList) {
				toppedNewsIdList.add(ft.getId());
			}

			if (dataSize > pageSize) {

				/*
				 * this part is pretty confusing. need to consider carefully
				 * before
				 * changing the logic. you could draw a diagram for detail
				 * analysis.
				 */
				if (toppedNewsSize < pageSize) {
					if (first >= toppedNewsSize) {
						result = newsEjb.getNewsListByCategory(parentCategory, first - toppedNewsSize, pageSize, toppedNewsIdList, isUserAccessableToLockedNews);
					} else {
						result.addAll(toppedNewsList);
						result.addAll(newsEjb.getNewsListByCategory(parentCategory, first, pageSize - toppedNewsSize, toppedNewsIdList, isUserAccessableToLockedNews));
					}

				} else {
					if (first >= toppedNewsSize) {
						result = newsEjb.getNewsListByCategory(parentCategory, first - toppedNewsSize, pageSize, toppedNewsIdList, isUserAccessableToLockedNews);
					} else if (first + pageSize < toppedNewsSize) {
						result = toppedNewsList.subList(first, first + pageSize);
					} else {
						/*
						 * when first<stickthreadsize and first+pagesize
						 * >stickthreadsize
						 */
						result.addAll(toppedNewsList.subList(first, toppedNewsSize));
						result.addAll(newsEjb.getNewsListByCategory(parentCategory, 0, pageSize - (toppedNewsSize - first), toppedNewsIdList, isUserAccessableToLockedNews));
					}

				}
			} else {
				result.addAll(toppedNewsList);
				result.addAll(newsEjb.getNewsListByCategory(parentCategory, 0, dataSize - toppedNewsSize, toppedNewsIdList, isUserAccessableToLockedNews));
			}
		}
		return result;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}