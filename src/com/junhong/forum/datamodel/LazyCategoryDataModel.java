/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.service.CategoryServiceSingleton;

/**
 * @author zhanjung
 * 
 */

public class LazyCategoryDataModel extends LazyDataModel<ForumCategory> {

	private static final long			serialVersionUID	= -770557034492199270L;

	private int							parentCategoryId;

	@EJB
	private CategoryServiceSingleton	categoryServiceSingleton;

	public LazyCategoryDataModel() {
	}

	public LazyCategoryDataModel(int categoryId) {
		this.parentCategoryId = categoryId;
	}

	@Override
	public List<ForumCategory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		List<ForumCategory> result;
		Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		ForumCategory tempCategory = (ForumCategory) session.get(Constants.CURRENT_CATEGORY);
		if (null != tempCategory) {
			parentCategoryId = tempCategory.getId();
		}

		if (parentCategoryId == -1) {
			// rowCount
			int dataSize = categoryServiceSingleton.getTotalTopCategoryCount();
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = categoryServiceSingleton.findTopCategories(first, first + pageSize);
			} else {
				result = categoryServiceSingleton.getAllTopCategories();
			}

		} else {
			int dataSize = (int) categoryServiceSingleton.getTotalSubcategoryCount(parentCategoryId);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = categoryServiceSingleton.findSubCategories(parentCategoryId, first, first + pageSize);
			} else {
				result = categoryServiceSingleton.findAllSubCategories(parentCategoryId);
			}
		}
		return result;

	}

	public int getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(int parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

}