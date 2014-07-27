package com.junhong.forum.entity;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.DisplayTypeMainPage;
import com.junhong.forum.constaint.CategoryNameUniqueness;

@Dependent
@Entity
@Table(name = "category")
@NamedQueries({ @NamedQuery(name = "forumcategory.all", query = "select distinct c from ForumCategory as c order by c.displaySequence") })
@CategoryNameUniqueness
public class ForumCategory extends AbstractEntity {
	private static final long	serialVersionUID			= 01l;

	/* ------------instance Variable-------------- */
	@NotNull
	@Size(min = 3, message = "{com.junhong.forum.entity.forumCategoryName}")
	private String				categoryName;
	@Column(length = 1000)
	private String				description;
	private int					numOfThread;
	// for top categories
	private int					displaySequence;
	// the owner of the category
	@ManyToOne
	@JoinColumn(name = "owner_Id")
	private User				owner						= new User();
	@ManyToOne
	@JoinColumn(name = "parentCategory_Id")
	private ForumCategory		parentCategory;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parentCategory", cascade = { CascadeType.REMOVE })
	private List<ForumCategory>	childrenCategory;
	@OneToMany(mappedBy = "category", cascade = { CascadeType.REMOVE })
	private List<ForumThread>	threadList;
	// control if/how this category will be displayed on the main index page.
	@Enumerated(EnumType.STRING)
	private DisplayTypeMainPage	displayTypeOnMainIndexPage	= DisplayTypeMainPage.NONE;
	private boolean				isGlobalAnnouncement		= false;

	/* -------------constructor----------------- */

	public ForumCategory() {
	}

	/* -------------business logic----------------- */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForumCategory other = (ForumCategory) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}

	/* -------------getter/setter----------------- */
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public ForumCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ForumCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	public List<ForumCategory> getChildrenCategory() {
		return childrenCategory;
	}

	public void setChildrenCategory(List<ForumCategory> childrenCategory) {
		this.childrenCategory = childrenCategory;
	}

	public List<ForumThread> getThreadList() {
		return threadList;
	}

	public void setThreadList(List<ForumThread> threadList) {
		this.threadList = threadList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumOfThread() {
		return numOfThread;
	}

	/**
	 * there are multiple threaded created for the same category, then making
	 * this method synchronized to make sure only one will update the
	 * numOfThread at a time
	 * 
	 * @param numOfThread
	 */
	public synchronized void setNumOfThread(int numOfThread) {
		this.numOfThread = numOfThread;
	}

	public DisplayTypeMainPage getDisplayTypeOnMainIndexPage() {
		return displayTypeOnMainIndexPage;
	}

	public void setDisplayTypeOnMainIndexPage(DisplayTypeMainPage displayTypeOnMainIndexPage) {
		this.displayTypeOnMainIndexPage = displayTypeOnMainIndexPage;
	}

	public int getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}

	public boolean isGlobalAnnouncement() {
		return isGlobalAnnouncement;
	}

	public void setGlobalAnnouncement(boolean isGlobalAnnouncement) {
		this.isGlobalAnnouncement = isGlobalAnnouncement;
	}

}
