/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.entity.CashBackWithdrawRequest;

/**
 * @author zhanjung
 * 
 */

public class LazyCashBackWithdrawRequestDataModel extends LazyDataModel<CashBackWithdrawRequest> {

	@EJB
	private UserEjb	userEjb;
	private boolean	isAuthorized	= true;
	private User	user			= null;

	public LazyCashBackWithdrawRequestDataModel() {
	}

	@Override
	public List<CashBackWithdrawRequest> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<CashBackWithdrawRequest> result = new ArrayList<CashBackWithdrawRequest>();
		if (isAuthorized) {
			if (null == filters || filters.isEmpty()) {
				int dataSize = (int) userEjb.getTotalCashBackWithdrawRequestCount(user);
				this.setRowCount(dataSize);
				// paginatet
				result = userEjb.findCashBackWithdrawRequest(user, first, first + pageSize);
			} else {
				int dataSize = userEjb.getTotalCashBackWithdrawRequestCount(user, filters);
				this.setRowCount(dataSize);
				result = userEjb.getCashBackWithdrawList(user, first, pageSize, filters);
			}
		}
		return result;

	}

	public boolean isAuthorized() {
		return isAuthorized;
	}

	public void setAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}