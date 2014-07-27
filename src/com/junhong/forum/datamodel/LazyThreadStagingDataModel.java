/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.forum.entity.ThreadStaging;
import com.junhong.forum.service.ThreadStagingEjb;

/**
 * @author zhanjung
 * 
 */

public class LazyThreadStagingDataModel extends LazyDataModel<ThreadStaging> {

	@EJB
	private ThreadStagingEjb	threadStagingEjb;
	private boolean				isAuthorized	= true;

	public LazyThreadStagingDataModel() {
	}

	@Override
	public List<ThreadStaging> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<ThreadStaging> result = new ArrayList<ThreadStaging>();
		if (isAuthorized) {
			if (null == filters || filters.isEmpty()) {
				int dataSize = threadStagingEjb.getTotalThreadStagingCount();
				this.setRowCount(dataSize);
				// paginatet
				result = threadStagingEjb.getThreadStagingList(first, pageSize);
			} else {
				boolean isValid = validateFilterValues(filters);
				if (isValid) {
					int dataSize = threadStagingEjb.getTotalThreadStagingCount(filters);
					this.setRowCount(dataSize);
					result = threadStagingEjb.getThreadStagingList(first, pageSize, filters);
				}

			}
		}
		return result;

	}

	public boolean validateFilterValues(Map<String, Object> filters) {
		boolean result = true;
		String filterProperty = null;
		Object filterValue = null;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("createTime")) {
				String dateRxp = "^(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])$";
				Pattern pattern = Pattern.compile(dateRxp);
				Matcher matcher = pattern.matcher((String) filterValue);
				if (!matcher.matches()) {
					result = false;
					break;
				}

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

}