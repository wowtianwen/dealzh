package com.junhong.forum.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: InternalLink
 * 
 */
@Entity
@Dependent
@Table(name = "InternalLink")
public class InternalLink extends AbstractEntity implements Serializable {
	private static final long	serialVersionUID	= -5534917516746372927L;
	@Column(length = 800)
	private String				linkURL;
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store				store;
	@Column(scale = 3)
	private BigDecimal			cashBackPct;
	@ManyToOne()
	@JoinColumn(name = "thread_id")
	private ForumThread			thread;

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public BigDecimal getCashBackPct() {
		return cashBackPct;
	}

	public void setCashBackPct(BigDecimal cashBackPct) {
		this.cashBackPct = cashBackPct;
	}

	public ForumThread getThread() {
		return thread;
	}

	public void setThread(ForumThread thread) {
		this.thread = thread;
	}
}
