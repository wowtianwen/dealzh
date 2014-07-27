package com.junhong.forum.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

import com.junhong.auth.entity.User;
import com.junhong.forum.common.RecordStatus;

/**
 * Entity implementation class for Entity: OrderInquery
 * 
 */
@Entity
@Dependent
@Table(name = "orderInquery")
@NamedQueries({ @NamedQuery(name = "orderInquery.all", query = "select s from OrderInquery as s") })
public class OrderInquery extends AbstractEntity implements Serializable {
	private static final long	serialVersionUID	= -1931090144052418420L;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User				user;
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store				store;
	@Temporal(TemporalType.DATE)
	private Date				purchaseDate;
	private String				orderNumber;
	@Column(scale = 2)
	private BigDecimal			orderAmount;
	@Enumerated(EnumType.STRING)
	private RecordStatus		status;
	@Column(scale = 2)
	private BigDecimal			cashBackAmount;
	@Temporal(TemporalType.DATE)
	private Date				efftDate;
	private String				note;
	@ManyToOne
	@JoinColumn(name = "createdBy")
	private User				createdBy;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	public BigDecimal getCashBackAmount() {
		return cashBackAmount;
	}

	public void setCashBackAmount(BigDecimal cashBackAmount) {
		this.cashBackAmount = cashBackAmount;
	}

	public Date getEfftDate() {
		return efftDate;
	}

	public void setEfftDate(Date efftDate) {
		this.efftDate = efftDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

}
