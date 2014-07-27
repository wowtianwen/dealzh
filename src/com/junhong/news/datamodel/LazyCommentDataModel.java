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

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.news.ejb.CommentEjb;
import com.junhong.news.entity.Comment;
import com.junhong.news.entity.News;

/**
 * @author zhanjung
 * 
 */

public class LazyCommentDataModel extends LazyDataModel<Comment> {

	private News		news;

	private User		user;

	@EJB
	private CommentEjb	commentEjb;

	private boolean		loadLastPage	= false;

	public LazyCommentDataModel() {
	}

	public LazyCommentDataModel(News news) {
		this.news = news;
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
	public List<Comment> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		List<Comment> result = new ArrayList<Comment>();

		// handle normal request: all the replys for given thread
		// rowCount
		if (news == null) {
			result.clear();
			return result;
		}
		int dataSize = commentEjb.getTotalCountOfComment(news);
		this.setRowCount(dataSize);

		// calculate to load the last page
		if (loadLastPage) {
			if (dataSize == 0) {
				first = 0;
			} else {

				int mod = dataSize % pageSize;
				if (mod == 0) {
					first = (dataSize / pageSize - 1) * pageSize;

				} else {

					first = dataSize / pageSize * pageSize;
				}
			}
			loadLastPage = false;
		}

		// paginate
		if (dataSize > pageSize) {
			result = commentEjb.getCommentsByNews(news, first, first + pageSize);
		} else {
			result = commentEjb.getCommentsByNews(news);
		}
		return result;
	}

	public boolean isLoadLastPage() {
		return loadLastPage;
	}

	public void setLoadLastPage(boolean loadLastPage) {
		this.loadLastPage = loadLastPage;
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

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

}