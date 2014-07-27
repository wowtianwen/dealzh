package com.junhong.backingbean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.service.CategoryServiceSingleton;

@Named
@ApplicationScoped
public class MenuBean {
	private MenuModel			menuModel;
	// for all category list on the indexthread4allcategory
	@Inject
	CategoryServiceSingleton	forumCategoryEjb;

	@PostConstruct
	public void init() {
		menuModel = new DefaultMenuModel();
		List<ForumCategory> topForumCategoryList = forumCategoryEjb.getAllTopCategories();
		for (ForumCategory topCategory : topForumCategoryList) {
			buildMenu(menuModel, topCategory);
		}

	}

	/**
	 * build Forum Menu
	 * 
	 * @param parentMenu
	 * @param parentForumCategory
	 */
	public void buildMenu(MenuModel parentMenu, ForumCategory parentForumCategory) {
		if (parentForumCategory.getChildrenCategory() != null && parentForumCategory.getChildrenCategory().size() > 0) {
			List<ForumCategory> categoryList = parentForumCategory.getChildrenCategory();
			for (ForumCategory category : categoryList) {
				buildMenu(parentMenu, category);
			}
		} else {
			if (!parentForumCategory.isGlobalAnnouncement()) {
				DefaultMenuItem item = new DefaultMenuItem();
				item.setValue(parentForumCategory.getCategoryName());
				// item.setIcon("ui-icon-play");
				int categoryId = parentForumCategory.getId();
				item.setCommand("#{threadBackingBean.openListThreads(" + categoryId + ")}");
				parentMenu.addElement(item);
			}

		}

	}

	public void buildMenu4CategoriesFilter(MenuModel parentMenu, ForumCategory parentForumCategory) {
		if (parentForumCategory.getChildrenCategory() != null && parentForumCategory.getChildrenCategory().size() > 0) {
			List<ForumCategory> categoryList = parentForumCategory.getChildrenCategory();
			for (ForumCategory category : categoryList) {
				buildMenu(parentMenu, category);
			}
		} else {
			DefaultMenuItem item = new DefaultMenuItem();
			item.setValue(parentForumCategory.getCategoryName());
			// item.setIcon("ui-icon-play");
			int categoryId = parentForumCategory.getId();
			item.setCommand("#{threadBackingBean.setCategoryFilter(" + categoryId + ")}");
			item.setUpdate("@form");
			parentMenu.addElement(item);
		}

	}

	public MenuModel getMenuModel() {
		return menuModel;
	}

	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

}
