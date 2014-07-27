package com.junhong.news.entity;

import com.junhong.forum.entity.AbstractEntity;
import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: NewsDashBoard
 * 
 */
@Dependent
@Entity
@Table(name = "newsdashboard")
@NamedQuery(name = "newsdashboard.all", query = "select ndb from NewsDashBoard ndb  order by ndb.newsCategoryId asc ,ndb.createTime desc")
public class NewsDashBoard extends AbstractEntity implements Serializable {
	private int					newsId;
	private String				newsSubject;
	private String				newsDescription;
	private int					newsCategoryId;
	private String				picturePathURL;
	private boolean				isPictureNews;

	private static final long	serialVersionUID	= 1L;

	public NewsDashBoard() {
		super();
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public String getNewsSubject() {
		return newsSubject;
	}

	public void setNewsSubject(String newsSubject) {
		this.newsSubject = newsSubject;
	}

	public String getNewsDescription() {
		return newsDescription;
	}

	public void setNewsDescription(String newsDescription) {
		this.newsDescription = newsDescription;
	}

	public int getNewsCategoryId() {
		return newsCategoryId;
	}

	public void setNewsCategoryId(int newsCategoryId) {
		this.newsCategoryId = newsCategoryId;
	}

	public String getPicturePathURL() {
		return picturePathURL;
	}

	public void setPicturePathURL(String picturePathURL) {
		this.picturePathURL = picturePathURL;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isPictureNews() {
		return isPictureNews;
	}

	public void setPictureNews(boolean isPictureNews) {
		this.isPictureNews = isPictureNews;
	}

}
