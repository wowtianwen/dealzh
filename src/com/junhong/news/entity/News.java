package com.junhong.news.entity;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.junhong.auth.entity.User;
import com.junhong.forum.entity.AbstractEntity;

@Dependent
@Entity
@Table(name = "news")
public class News extends AbstractEntity {

	@NotNull()
	@Size(min = 1, message = "{com.junhong.news.entity.news.subject}")
	private String			subject;
	@Column(length = 60000)
	private String			content;
	private int				numOfComments;
	private int				numOfViews;
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User			owner;
	@OneToMany(mappedBy = "news")
	private List<Comment>	comments;
	@ManyToOne()
	@JoinColumn(name = "category_id")
	private NewsCategory	newsCategory;
	private boolean			locked;
	private boolean			isTopped;
	private Date			toppedTime;
	private boolean			onDashBoard;
	private int				votes;
	private String			thumbPicURL;
	private String			description;

	public News() {
		// TODO Auto-generated constructor stub
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getNumOfComments() {
		return numOfComments;
	}

	public void setNumOfComments(int numOfComments) {
		this.numOfComments = numOfComments;
	}

	public int getNumOfViews() {
		return numOfViews;
	}

	public void setNumOfViews(int numOfViews) {
		this.numOfViews = numOfViews;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public NewsCategory getNewsCategory() {
		return newsCategory;
	}

	public void setNewsCategory(NewsCategory newsCategory) {
		this.newsCategory = newsCategory;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isOnDashBoard() {
		return onDashBoard;
	}

	public void setOnDashBoard(boolean onDashBoard) {
		this.onDashBoard = onDashBoard;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public Date getToppedTime() {
		return toppedTime;
	}

	public void setToppedTime(Date toppedTime) {
		this.toppedTime = toppedTime;
	}

	public boolean isTopped() {
		return isTopped;
	}

	public void setTopped(boolean isTopped) {
		this.isTopped = isTopped;
	}

	public String getThumbPicURL() {
		return thumbPicURL;
	}

	public void setThumbPicURL(String thumbPicURL) {
		this.thumbPicURL = thumbPicURL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
