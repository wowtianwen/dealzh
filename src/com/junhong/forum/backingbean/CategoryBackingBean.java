/**
 * all the categories are cached when the system start up.
 * 
 */
package com.junhong.forum.backingbean;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.DisplayTypeMainPage;
import com.junhong.forum.common.Messages;
import com.junhong.forum.datamodel.LazyCategoryDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.util.Timing;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
@Timing
public class CategoryBackingBean extends AbstractBacking {

	private static final long			serialVersionUID	= -1369556498729315077L;

	private long						parentCategoryId;

	@Inject
	private Logger						logger;

	// represent the category
	// selected when you edit
	// the category
	@Inject
	private ForumCategory				selectedForumCategory;

	@Inject
	private UserEjb						userEjb;

	@Inject
	private CategoryServiceSingleton	categoryEjb;

	@Inject
	private LazyCategoryDataModel		lazyCategoryDataModel;

	// total number of record for paging
	int									totalListSize		= 0;

	// publish user's current position so that the observers can calculate the
	// navigation map.
	@Inject
	private Event<ForumCategory>		currentPositionEvent;

	/* ------------instance Variable-------------- */

	@PostConstruct
	public void initialize() {
		totalListSize = (int) categoryEjb.getTotalTopCategoryCount();
	}

	/* -------------business logic----------------- */

	/**
	 * 
	 */
	public String loadCategory() {
		String idStr = (String) this.getFlash().get("id");
		long id = -1;
		id = Long.parseLong(idStr);

		selectedForumCategory = categoryEjb.getForumCategoryById(id);
		if (selectedForumCategory.getParentCategory() != null) {
			this.setParentCategoryId(selectedForumCategory.getParentCategory().getId());
		} else {
			this.setParentCategoryId(0);
		}
		// this.getFlash().put("selectedCategory", selectedForumCategory);
		return "editcategory";
	}

	/**
	 * create the category
	 * 
	 * @return
	 */
	public String createCategory() {
		// ForumCategory newCategory = new ForumCategory(forumCategory);
		if (parentCategoryId != 0) {
			ForumCategory parentCategory = categoryEjb.getForumCategoryById(parentCategoryId);
			selectedForumCategory.setParentCategory(parentCategory);
		}

		User user = this.getCurrentUser();
		selectedForumCategory.setOwner(user);
		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, selectedForumCategory);

		// this.getFlash().put("navCatId", Long.toString(parentCategoryId));
		if (hasError) {
			return null;
		} else {
			return Constants.NavSuccess;
		}

	}

	/**
	 * @return
	 */
	public String updateCategory() {
		ForumCategory oldCategory = categoryEjb.getForumCategoryById(selectedForumCategory.getId());
		if (parentCategoryId != 0) {

			// set the current category's new parent category
			ForumCategory parentCategory = categoryEjb.getForumCategoryById(parentCategoryId);
			oldCategory.setParentCategory(parentCategory);
		} else {
			oldCategory.setParentCategory(null);
		}
		boolean hasError = this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, oldCategory);
		if (logger.isDebugEnabled()) {
			logger.debug("update CATEGORY.................{}", selectedForumCategory.getDescription());
		}
		if (hasError) {
			// authorization fail, then restore the category's parentcategory
			categoryEjb.init();
			return null;
		} else {
			return Constants.NavSuccess;
		}

	}

	/**
	 * @param category
	 */
	public String deleteCategory() {

		logger.debug("deleting category id {}", this.selectedForumCategory.getId());
		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, this.selectedForumCategory);
		return null;
	}

	public boolean isAdminOrOwner() {
		return true;
	}

	/**
	 * set the display type on the website Index page
	 * 
	 * @param forumCategory
	 * @param displayTypeOnMainIndexPage
	 */
	public void changeDisplayTypeOnMainIndexPage(ForumCategory forumCategory, String displayTypeOnMainIndexPage) {

		// refresh the state from the database
		forumCategory = categoryEjb.refreshCategory(forumCategory);
		// set the displayType
		if (displayTypeOnMainIndexPage.equalsIgnoreCase(Constants.Display_Type_Regular_Main_Index)) {
			forumCategory.setDisplayTypeOnMainIndexPage(DisplayTypeMainPage.REGULAR);
		} else if (displayTypeOnMainIndexPage.equalsIgnoreCase(Constants.Display_Type_Mixed_Main_Index)) {
			forumCategory.setDisplayTypeOnMainIndexPage(DisplayTypeMainPage.MIXED);
		}
		// update the database
		this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, forumCategory);
	}

	/* -------------getter/setter----------------- */

	public long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	/**
	 * load the top categories for the given category Id;
	 */
	public void loadTopCategories() {
		int currentCategoryId = -1;
		Object tempctgId = this.getFlash().get("navCatId");
		if (null == tempctgId || ((String) tempctgId).equalsIgnoreCase(Constants.Blank_String)) {
			this.setMessageAndRender("NO_CATEGORY_SELECTED");
		} else {
			int currctgId = Integer.parseInt((String) tempctgId);
			if (currctgId != -1) {
				// if selectedCategoryId changed, then reset htmldatatable.
				currentCategoryId = currctgId;
				// this.getNavigationMapBackingBean().calculateNavitationMap(selectedCategoryId);
			} else {
				currentCategoryId = -1;
				// this.getNavigationMapBackingBean().getNavigationMap().clear();
			}

			ForumCategory currentCategory = null;
			if (currentCategoryId != -1) {
				currentCategory = categoryEjb.getForumCategoryById(currentCategoryId);
				// put it in the session map so that it can be used to
			} else {
				currentCategory = new ForumCategory();
				currentCategory.setId(-1);
			}
			// set current category in the session so that it can be used to
			// retrieve sub_categories under the current category

			this.getSessionMap().put(Constants.CURRENT_CATEGORY, currentCategory);

			// update navigation map
			currentPositionEvent.fire(currentCategory);

		}
	}

	public void checkUniqueueness(FacesContext context, UIComponent comp, Object value) {
		ForumCategory category = new ForumCategory();
		category.setCategoryName((String) value);
		category.setId(this.selectedForumCategory.getId());
		if (category.getCategoryName() != null && categoryEjb.isCategoryExists(category)) {
			FacesMessage message = Messages.getMessage("", "duplicateCategory", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(comp.getClientId(), message);
			context.renderResponse();
		}

	}

	public ForumCategory getSelectedForumCategory() {
		return selectedForumCategory;
	}

	public void setSelectedForumCategory(ForumCategory selectedForumCategory) {
		this.selectedForumCategory = selectedForumCategory;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		ForumCategory category = (ForumCategory) entity;
		if (action.equals(Constants.Action_UPDATE)) {
			categoryEjb.updateForumCategory(category);
			categoryEjb.init();
		}
		if (action.equals(Constants.Action_CREATE)) {
			categoryEjb.createForumCategory(category);
			categoryEjb.init();

		}

		if (action.equals(Constants.Action_DELETE)) {
			categoryEjb.deleteForumCategory(category);
			categoryEjb.init();
		}
	}

	public String assignCategoryOwner() {
		String userId = (String) this.getRequestMap().get(Constants.NEWUSERID);
		User user = userEjb.getUserByUserId(userId);
		selectedForumCategory.setOwner(user);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedForumCategory);
		return null;

	}

	public LazyCategoryDataModel getLazyCategoryDataModel() {
		return lazyCategoryDataModel;
	}

	public void setLazyCategoryDataModel(LazyCategoryDataModel lazyCategoryDataModel) {
		this.lazyCategoryDataModel = lazyCategoryDataModel;
	}

}
