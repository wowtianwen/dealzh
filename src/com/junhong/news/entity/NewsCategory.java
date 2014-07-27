package com.junhong.news.entity;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
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

import com.junhong.auth.entity.User;
import com.junhong.forum.common.DisplayType;
import com.junhong.forum.common.DisplayTypeMainPage;
import com.junhong.forum.entity.AbstractEntity;

@Named
@Dependent
@Entity()
@Table(name = "newscategory")
@NamedQueries({ @NamedQuery(name = "newscategory.all", query = "select distinct c from NewsCategory as c") })
public class NewsCategory extends AbstractEntity {
	private static final long	serialVersionUID			= 1L;

	private String				categoryName;
	@Column(length = 1000)
	private String				description;
	private int					numOfNews;
	@ManyToOne
	@JoinColumn(name = "owner_Id")
	private User				categoryOwner;
	@OneToMany(mappedBy = "newsCategory")
	private List<News>			news;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parentCategory")
	private List<NewsCategory>	childrenCategory;

	@ManyToOne
	private NewsCategory		parentCategory;

	// display type on the news mainpage
	@Enumerated(EnumType.STRING)
	private DisplayType			displayType					= DisplayType.REGULAR;

	// display type on the website index page, it control how
	@Enumerated(EnumType.STRING)
	private DisplayTypeMainPage	displayTypeOnMainIndexPage	= DisplayTypeMainPage.NONE;

	public NewsCategory() {
		// TODO Auto-generated constructor stub
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumOfNews() {
		return numOfNews;
	}

	public void setNumOfNews(int numOfNews) {
		this.numOfNews = numOfNews;
	}

	public User getCategoryOwner() {
		return categoryOwner;
	}

	public void setCategoryOwner(User categoryOwner) {
		this.categoryOwner = categoryOwner;
	}

	public List<News> getNews() {
		return news;
	}

	public void setNews(List<News> news) {
		this.news = news;
	}

	public NewsCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(NewsCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	public List<NewsCategory> getChildrenCategory() {
		return childrenCategory;
	}

	public void setChildrenCategory(List<NewsCategory> childrenCategory) {
		this.childrenCategory = childrenCategory;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public DisplayTypeMainPage getDisplayTypeOnMainIndexPage() {
		return displayTypeOnMainIndexPage;
	}

	public void setDisplayTypeOnMainIndexPage(DisplayTypeMainPage displayTypeOnMainIndexPage) {
		this.displayTypeOnMainIndexPage = displayTypeOnMainIndexPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		NewsCategory other = (NewsCategory) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}

}
