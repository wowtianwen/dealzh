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
import com.junhong.forum.entity.OrderInquery;

/**
 * @author zhanjung
 * 
 */

public class LazyOrderInqueryDataModel extends LazyDataModel<OrderInquery> {

	@EJB
	private UserEjb	userEjb;
	private boolean	isAuthorized	= true;
	private User	user			= null;

	public LazyOrderInqueryDataModel() {
	}

	@Override
	public List<OrderInquery> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<OrderInquery> result = new ArrayList<OrderInquery>();
		if (isAuthorized) {
			int dataSize = userEjb.getTotalOrderInqueryCount(user, filters);
			this.setRowCount(dataSize);
			result = userEjb.getOrderInqueryList(user, first, pageSize, filters);
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