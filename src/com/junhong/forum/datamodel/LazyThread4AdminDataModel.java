/**
 * jsf_demo
 * zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.Collections;
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
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadSorter;
import com.junhong.forum.service.ThreadEjb;

/**
 * @author zhanjung
 * 
 */
public class LazyThread4AdminDataModel extends LazyDataModel<ForumThread> {

	private ForumCategory	parentCategory;

	private User			user;

	@EJB
	private ThreadEjb		threadEjb;

	@Inject
	private Login			login;

	public LazyThread4AdminDataModel() {
	}

	public LazyThread4AdminDataModel(ForumCategory category) {
		this.parentCategory = category;
	}

	@Override
	public List<ForumThread> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put(Constants.DATATABLE_FIRST, first);
		// fix issue of reset to pendingforreview when page is backed from
		// rejected thread page

		// filters is empty only the it return back from view rejected thread
		// page or user is the first time to go to managePendingThead page
		if (filters.isEmpty()) {
			Map<String, Object> previousFilters = (Map<String, Object>) sessionMap.get(Constants.PENDING_THREAD_STATUS);
			if (null != previousFilters && !previousFilters.isEmpty()) {
				filters = previousFilters;
			}
		} else {
			sessionMap.put(Constants.PENDING_THREAD_STATUS, filters);
		}
		// fetch all pending threads regardless its category
		int dataSize = threadEjb.getNonApprovedForumThreadCount4AllPCategory(filters);
		this.setRowCount(dataSize);
		result = threadEjb.getNonApprovedForumThread4AllPCategory(first, pageSize, filters);
		// paginate

		// sort
		if (sortField != null) {
			Collections.sort(result, new ThreadSorter(sortField, sortOrder));
		}
		return result;
	}

	public ForumCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ForumCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.SOURCE_LAZY_THREAD_DATA_MODEL, source);
	}

	public void setUser(User user) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.USER_LAZY_THREAD_DATA_MODEL, user);
	}

}