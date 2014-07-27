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

import com.junhong.forum.entity.Store;
import com.junhong.forum.service.StoreEjb;
import com.junhong.util.StringUtil;

/**
 * @author zhanjung
 * 
 */

public class LazyStoreDataModel extends LazyDataModel<Store> {

	@EJB
	private StoreEjb storeEjb;
	private boolean isAuthorized = true;
	private String filter = null;

	public LazyStoreDataModel() {
	}

	@Override
	public List<Store> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<Store> result = new ArrayList<Store>();
		if (isAuthorized) {
			if (StringUtil.isNullOrBlank(filter)) {
				int dataSize = storeEjb.getTotalStoreCount();
				this.setRowCount(dataSize);
				// paginatet
				result = storeEjb.getStoreList(first, first + pageSize);
			} else {
				this.setRowCount(10);
				result.add(storeEjb.getStoreByName(filter));
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

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}