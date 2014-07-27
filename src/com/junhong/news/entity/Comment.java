package com.junhong.news.entity;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.junhong.auth.entity.User;

@Dependent
@Entity
@Table(name = "comment")
public class Comment extends com.junhong.forum.entity.AbstractEntity {

	private static final long	serialVersionUID	= 5895530924961201637L;
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User				owner;
	private String				subject;
	@Column(length = 10000)
	private String				content;
	@ManyToOne
	@JoinColumn(name = "news_id")
	private News				news;

	@Transient
	boolean						editable;

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
