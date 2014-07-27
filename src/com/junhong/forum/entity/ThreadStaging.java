package com.junhong.forum.entity;

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.junhong.forum.common.RecordStatus;

@Dependent
@Entity
@Table(name = "threadStaging")
@NamedQueries({ @NamedQuery(name = "threadStaging.all", query = "select t from ThreadStaging as t") })
@XmlRootElement(name = "thread")
public class ThreadStaging extends AbstractEntity {
	private static final long	serialVersionUID	= 01l;

	/* ------------instance Variable-------------- */
	@NotNull()
	@Size(min = 1, message = "{com.junhong.news.entity.news.subject}")
	@Column(length = 1000)
	private String				subject;
	private String				externalId;
	@Column(length = 60000)
	private String				content;
	@Column(length = 1000)
	private String				thumbPicURL;
	/* relationship to other Entities */
	private String				categoryName;
	@Pattern(regexp = "(^\\$?(([1-9]\\d{0,2}(,?\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d\\d)?)|([0-9]{1,2}%\\s+((?i)(off))$)|(\\s)*", message = "{InvalidPrice}")
	private String				price;
	@Temporal(TemporalType.DATE)
	private Date				startDate;
	@Temporal(TemporalType.DATE)
	private Date				endDate;
	@Enumerated(EnumType.STRING)
	private RecordStatus		status				= RecordStatus.PENDING;
	@ManyToOne
	@JoinColumn(name = "storeId")
	private Store				store;
	@Column(length = 1000)
	private String				dealURL;

	private String				advertiserId;

	/* -------------Constructor----------------- */
	public ThreadStaging() {

	}

	/* -------------business logic----------------- */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		ThreadStaging other = (ThreadStaging) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
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

	public String getThumbPicURL() {
		return thumbPicURL;
	}

	public void setThumbPicURL(String thumbPicURL) {
		this.thumbPicURL = thumbPicURL;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getDealURL() {
		return dealURL;
	}

	public void setDealURL(String dealURL) {
		this.dealURL = dealURL;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}

}
