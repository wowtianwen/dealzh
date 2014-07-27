/**
 * jsf_demo
 * zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.service.ReplyEjb;

/**
 * @author zhanjung
 * 
 */
public class LazyReplyDataModel extends LazyDataModel<ForumReply> {
	private static final long	serialVersionUID	= 01l;

	private ForumThread			thread;

	private User				user;

	@EJB
	private ReplyEjb			replyEjb;

	// check where it is from: MAIN, or MYPOST in the user profile page
	private String				source				= "-1";

	private boolean				loadLastPage		= false;

	public LazyReplyDataModel() {
	}

	public LazyReplyDataModel(ForumThread thread) {
		this.thread = thread;
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
	public List<ForumReply> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		List<ForumReply> result = new ArrayList<ForumReply>();
		// handle the request from MyReply tab
		if (this.source.equalsIgnoreCase(Constants.USER_TAB_MYREPLY)) {
			int dataSize = replyEjb.getTotalCountOfReply(user);
			this.setRowCount(dataSize);
			result = replyEjb.getReplyListByOwner(user, first, pageSize);
			return result;
		}

		// handle normal request: all the replys for given thread
		// rowCount
		thread = (ForumThread) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.BELONG_THREAD_ID);
		if (thread == null) {
			result.clear();
			return result;
		}
		int dataSize = replyEjb.getTotalCountOfReply(thread);
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
			result = replyEjb.getReplyListByThread(thread, first, first + pageSize);
		} else {
			result = replyEjb.getReplyListByThread(thread);
		}
		return result;
	}

	public ForumThread getThread() {
		return thread;
	}

	public void setThread(ForumThread thread) {
		this.thread = thread;
	}

	public boolean isLoadLastPage() {
		return loadLastPage;
	}

	public void setLoadLastPage(boolean loadLastPage) {
		this.loadLastPage = loadLastPage;
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