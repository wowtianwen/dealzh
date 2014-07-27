package com.junhong.forum.entity;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Dependent
@Table(name = "threaddashboard")
@NamedQuery(name = "threaddashboard.all", query = "select db from ThreadDashBoard db  order by  db.categoryId asc, db.createTime desc")
public class ThreadDashBoard extends AbstractEntity {
	private static final long	serialVersionUID	= 7854841321312315073L;
	private int					categoryId;
	private int					threadId;
	private String				threadSubject;
	private String				threadOwner;
	private String				picturePathURL;
	private String				price;
	private String				storeName;
	private boolean				markedHot			= false;
	@ManyToOne
	@JoinColumn(name = "store_Id")
	private Store				store;
	private String				shortContent;
	@Column(length = 6000)
	private String				fullContent;
	private int					votes;
	private String				buyLink;

	/**
	 * @return the categoryId
	 */
	public int getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the threadSubject
	 */
	public String getThreadSubject() {
		return threadSubject;
	}

	/**
	 * @param threadSubject
	 *            the threadSubject to set
	 */
	public void setThreadSubject(String threadSubject) {
		this.threadSubject = threadSubject;
	}

	/**
	 * @return the threadid
	 */
	public int getThreadId() {
		return threadId;
	}

	/**
	 * @param threadid
	 *            the threadid to set
	 */
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	/**
	 * @return the threadOwner
	 */
	public String getThreadOwner() {
		return threadOwner;
	}

	/**
	 * @param threadOwner
	 *            the threadOwner to set
	 */
	public void setThreadOwner(String threadOwner) {
		this.threadOwner = threadOwner;
	}

	public String getPicturePathURL() {
		return picturePathURL;
	}

	public void setPicturePathURL(String picturePathURL) {
		this.picturePathURL = picturePathURL;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public boolean isMarkedHot() {
		return markedHot;
	}

	public void setMarkedHot(boolean markedHot) {
		this.markedHot = markedHot;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getFullContent() {
		return fullContent;
	}

	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getShortContent() {
		return shortContent;
	}

	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}

	public String getBuyLink() {
		return buyLink;
	}

	public void setBuyLink(String buyLink) {
		this.buyLink = buyLink;
	}

}
