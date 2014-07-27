/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.forum.entity.Event;
import com.junhong.forum.service.EventServiceSingleton;

/**
 * @author zhanjung
 * 
 */

public class LazyEventDataModel extends LazyDataModel<Event> {
	@EJB
	private EventServiceSingleton eventServiceSingleton;

	public LazyEventDataModel() {
	}

	@Override
	public List<Event> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		List<Event> result;
		int dataSize = (int) eventServiceSingleton.getTotalEventCount();
		this.setRowCount(dataSize);

		// paginate
		result = eventServiceSingleton.getAllEvents(first, first + pageSize);
		return result;

	}

}