package com.junhong.news.backingbean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.DisplayType;
import com.junhong.forum.common.DisplayTypeMainPage;
import com.junhong.forum.common.Messages;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.news.datamodel.LazyNewsCategoryDataModel;
import com.junhong.news.ejb.NewsCategoryEjb;
import com.junhong.news.entity.NewsCategory;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
public class NewsCategoryBackingBean extends AbstractBacking {
	@Inject
	private Logger logger;

	private long parentCategoryId;

	@Inject
	private NewsCategory newsCategory;
	@Inject
	private UserEjb userEjb;

	@Inject
	private LazyNewsCategoryDataModel lazyNewsCategoryDataModel;

	@EJB
	private NewsCategoryEjb newCategoryEjb;

	// total number of record for paging
	int totalListSize = 0;

	// publish user's current position so that the observers can calculate the
	// navigation map.
	@Inject
	private Event<NewsCategory> currentPositionEvent;

	private List<NewsCategory> allCategories;

	public NewsCategoryBackingBean() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initialize() {
		totalListSize = (int) newCategoryEjb.getTotalTopCategoryCount();

		populateAllFlatCategories();
	}

	/**
	 * populate All Categories.
	 */
	private void populateAllFlatCategories() {
		this.allCategories = newCategoryEjb.getAllFlatCategories();
	}

	/**
	 * 
	 */
	public String loadCategory() {
		String idStr = (String) this.getFlash().get("id");
		long id = -1;
		id = Long.parseLong(idStr);

		newsCategory = newCategoryEjb.getNewsCategoryById(id);
		if (newsCategory.getParentCategory() != null) {
			this.setParentCategoryId(newsCategory.getParentCategory().getId());
		} else {
			this.setParentCategoryId(0);
		}
		// this.getFlash().put("selectedCategory", selectedForumCategory);
		return "editcategory";
	}

	public void loadTopCategories() {
		int currentCategoryId = -1;
		Object tempctgId = this.getFlash().get("navCatId");
		if (null == tempctgId || ((String) tempctgId).equalsIgnoreCase(Constants.Blank_String)) {
			this.setMessageAndRender("NO_CATEGORY_SELECTED");
		} else {
			int currctgId = Integer.parseInt((String) tempctgId);
			if (currctgId != -1) {
				currentCategoryId = currctgId;
			} else {
				currentCategoryId = -1;
			}

			NewsCategory currentCategory = null;
			if (currentCategoryId != -1) {
				currentCategory = newCategoryEjb.getNewsCategoryById(currentCategoryId);
				// put it in the session map so that it can be used to
			} else {
				currentCategory = new NewsCategory();
				currentCategory.setId(-1);
			}
			// set current category in the session so that it can be used to
			// retrieve sub_categories under the current category

			this.getSessionMap().put(Constants.CURRENT_NEWSCATEGORY, currentCategory);

			// update navigation map
			currentPositionEvent.fire(currentCategory);

		}
	}

	public void checkUniqueueness(FacesContext context, UIComponent comp, Object value) {
		NewsCategory category = new NewsCategory();
		category.setCategoryName((String) value);
		category.setId(this.newsCategory.getId());
		if (category.getCategoryName() != null && newCategoryEjb.isCategoryExists(category)) {
			FacesMessage message = Messages.getMessage("", "duplicateCategory", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(comp.getClientId(), message);
			context.renderResponse();
		}

	}

	public String assignCategoryOwner() {
		String userId = (String) this.getRequestMap().get(Constants.NEWUSERID);
		User user = userEjb.getUserByUserId(userId);
		newsCategory.setCategoryOwner(user);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, newsCategory);
		return null;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		NewsCategory category = (NewsCategory) entity;
		if (action.equals(Constants.Action_UPDATE)) {
			newCategoryEjb.updateNewsCategory(category);
			newCategoryEjb.init();

		}
		if (action.equals(Constants.Action_CREATE)) {
			newCategoryEjb.createNewsCategory(category);
			newCategoryEjb.init();

		}
		if (action.equals(Constants.Action_DELETE)) {
			newCategoryEjb.deleteNewsCategory(category);
			newCategoryEjb.init();
		}

	}

	/**
	 * create the category
	 * 
	 * @return
	 */
	public String createCategory() {
		// ForumCategory newCategory = new ForumCategory(forumCategory);
		if (parentCategoryId != 0) {
			NewsCategory parentCategory = newCategoryEjb.getNewsCategoryById(parentCategoryId);
			newsCategory.setParentCategory(parentCategory);
		}

		User user = this.getCurrentUser();
		newsCategory.setCategoryOwner(user);
		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, newsCategory);

		// this.getFlash().put("navCatId", Long.toString(parentCategoryId));
		if (hasError) {
			return null;
		} else {
			return Constants.NavSuccess;
		}

	}

	/**
	 * update Category
	 * 
	 * @return
	 */
	public String updateCategory() {
		NewsCategory oldCategory = newCategoryEjb.getNewsCategoryById(newsCategory.getId());
		oldCategory.setCategoryName(newsCategory.getCategoryName());
		oldCategory.setDescription(newsCategory.getDescription());

		if (parentCategoryId != 0) {

			// set the current category's new parent category
			NewsCategory parentCategory = newCategoryEjb.getNewsCategoryById(parentCategoryId);
			oldCategory.setParentCategory(parentCategory);
		} else {
			oldCategory.setParentCategory(null);
		}
		boolean hasError = this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, oldCategory);
		if (hasError) {
			newCategoryEjb.init();
			return null;
		} else {
			return Constants.NavSuccess;
		}

	}

	/**
	 * @param category
	 */
	public String deleteCategory() {

		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, this.newsCategory);
		return null;
	}

	/**
	 * set the display type on the news Index page
	 * 
	 * @param newsCategory
	 * @param displayType
	 */
	public void changeDisplayType(NewsCategory newsCategory, String displayType) {

		// refresh the state from the database
		newsCategory = newCategoryEjb.refreshNewsCategory(newsCategory);
		// set the displayType
		if (displayType.equalsIgnoreCase(Constants.Display_Type_Tab)) {
			newsCategory.setDisplayType(DisplayType.TAB);
		} else if (displayType.equalsIgnoreCase(Constants.Display_Type_Regular)) {
			newsCategory.setDisplayType(DisplayType.REGULAR);
		}

		// update the database
		this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, newsCategory);

	}

	/**
	 * set the display type on the website Index page
	 * 
	 * @param newsCategory
	 * @param displayTypeOnMainIndexPage
	 */
	public void changeDisplayTypeOnMainIndexPage(NewsCategory newsCategory, String displayTypeOnMainIndexPage) {

		// refresh the state from the database
		newsCategory = newCategoryEjb.refreshNewsCategory(newsCategory);
		// set the displayType
		if (displayTypeOnMainIndexPage.equalsIgnoreCase(Constants.Display_Type_Regular_Main_Index)) {
			newsCategory.setDisplayTypeOnMainIndexPage(DisplayTypeMainPage.REGULAR);
		} else if (displayTypeOnMainIndexPage.equalsIgnoreCase(Constants.Display_Type_Mixed_Main_Index)) {
			newsCategory.setDisplayTypeOnMainIndexPage(DisplayTypeMainPage.MIXED);
		}
		// update the database
		this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, newsCategory);
	}

	/* ---------getter/setter--------- */

	public long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public NewsCategory getNewsCategory() {
		return newsCategory;
	}

	public void setNewsCategory(NewsCategory newsCategory) {
		this.newsCategory = newsCategory;
	}

	public LazyNewsCategoryDataModel getLazyNewsCategoryDataModel() {
		return lazyNewsCategoryDataModel;
	}

	public void setLazyNewsCategoryDataModel(LazyNewsCategoryDataModel lazyNewsCategoryDataModel) {
		this.lazyNewsCategoryDataModel = lazyNewsCategoryDataModel;
	}

	public int getTotalListSize() {
		return totalListSize;
	}

	public void setTotalListSize(int totalListSize) {
		this.totalListSize = totalListSize;
	}

	public List<NewsCategory> getAllCategories() {
		return allCategories;
	}

	public void setAllCategories(List<NewsCategory> allCategories) {
		this.allCategories = allCategories;
	}

}
