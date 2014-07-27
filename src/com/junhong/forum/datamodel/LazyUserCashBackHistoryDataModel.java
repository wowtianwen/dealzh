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
import com.junhong.forum.common.UserCashBackRecordStatus;
import com.junhong.forum.entity.UserCashBackHistory;

/**
 * @author zhanjung
 * 
 */

public class LazyUserCashBackHistoryDataModel extends LazyDataModel<UserCashBackHistory> {

	@EJB
	private UserEjb						userEjb;
	private User						user;
	private UserCashBackRecordStatus	cashBackRecordStatus;

	public LazyUserCashBackHistoryDataModel() {
	}

	@Override
	public List<UserCashBackHistory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<UserCashBackHistory> result = new ArrayList<UserCashBackHistory>();
		if (user != null) {
			// for individual user
			int dataSize = userEjb.getTotalNumberOfUserCashBacks(user, cashBackRecordStatus, filters);
			this.setRowCount(dataSize);
			// paginatet
			result = userEjb.getUserCashBackHistory(user, first, pageSize, cashBackRecordStatus, filters);
		} else {
			// for admin to retrieve all user's cash back
			int dataSize = userEjb.getTotalNumberOfUserCashBacksForAllUsers(cashBackRecordStatus, filters);
			this.setRowCount(dataSize);
			// paginatet
			result = userEjb.getUserCashBackHistoryForAllUsers(first, pageSize, cashBackRecordStatus, filters);

		}
		return result;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserCashBackRecordStatus getCashBackRecordStatus() {
		return cashBackRecordStatus;
	}

	public void setCashBackRecordStatus(UserCashBackRecordStatus cashBackRecordStatus) {
		this.cashBackRecordStatus = cashBackRecordStatus;
	}

}