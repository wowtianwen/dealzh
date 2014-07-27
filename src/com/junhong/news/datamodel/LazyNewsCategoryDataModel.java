/**
 * jsf_demo zhanjung
 */
package com.junhong.news.datamodel;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.forum.common.Constants;
import com.junhong.news.ejb.NewsCategoryEjb;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */

public class LazyNewsCategoryDataModel extends LazyDataModel<NewsCategory> {

	private int				parentCategoryId;

	@EJB
	private NewsCategoryEjb	newsCategoryEjb;

	public LazyNewsCategoryDataModel() {
	}

	public LazyNewsCategoryDataModel(int categoryId) {
		this.parentCategoryId = categoryId;
	}

	@Override
	public List<NewsCategory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		List<NewsCategory> result;
		Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		NewsCategory tempCategory = (NewsCategory) session.get(Constants.CURRENT_NEWSCATEGORY);
		if (null != tempCategory) {
			parentCategoryId = tempCategory.getId();
		}

		if (parentCategoryId == -1) {
			// rowCount
			int dataSize = newsCategoryEjb.getTotalTopCategoryCount();
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = newsCategoryEjb.findTopCategories(first, first + pageSize);
			} else {
				result = newsCategoryEjb.getAllTopCategories();
			}

		} else {
			int dataSize = (int) newsCategoryEjb.getTotalSubcategoryCount(parentCategoryId);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = newsCategoryEjb.findSubCategories(parentCategoryId, first, first + pageSize);
			} else {
				result = newsCategoryEjb.findAllSubCategories(parentCategoryId);
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