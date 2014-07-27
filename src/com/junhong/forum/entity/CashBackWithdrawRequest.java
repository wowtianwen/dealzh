package com.junhong.forum.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.RecordStatus;

/**
 * Entity implementation class for Entity: Affilicate
 * 
 */
@Entity
@Dependent
@NamedQueries({ @NamedQuery(name = "cashbackwithdrawrequest.all", query = "select withdrawrequest from CashBackWithdrawRequest as withdrawrequest") })
public class CashBackWithdrawRequest extends AbstractEntity implements Serializable {
	private static final long	serialVersionUID	= -2079215778347725014L;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User				user;
	// pending or processed
	@Enumerated(EnumType.STRING)
	private RecordStatus		status;
	@Column(scale = 2)
	private BigDecimal			withdrawAmount;
	private String				paypalEmail;
	private String				note;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	public BigDecimal getWithdrawAmount() {
		return withdrawAmount;
	}

	public void setWithdrawAmount(BigDecimal withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	public String getPaypalEmail() {
		return paypalEmail;
	}

	public void setPaypalEmail(String paypalEmail) {
		this.paypalEmail = paypalEmail;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
