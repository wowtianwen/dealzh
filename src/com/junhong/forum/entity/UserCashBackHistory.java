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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.UserCashBackRecordStatus;

/**
 * Entity implementation class for Entity: Affilicate
 * 
 */
@Entity
@Dependent
@Table(name = "userCashBackHistory")
public class UserCashBackHistory extends AbstractEntity implements Serializable {
	private static final long			serialVersionUID	= -2809285970328254549L;
	@ManyToOne
	@JoinColumn(name = "userId")
	private User						user;
	@ManyToOne
	@JoinColumn(name = "storeId")
	private Store						store;
	@Column(scale = 2)
	private BigDecimal					cashBackAmount;
	private String						transactionId;
	@Temporal(TemporalType.DATE)
	private Date						transactionDate;
	@Column(scale = 2)
	private BigDecimal					transactionAmount;
	// pending/ready for pay or paid,
	@Enumerated(EnumType.STRING)
	private UserCashBackRecordStatus	status;
	// --if payment is received from network, this one will drive if commission
	// is payable to user
	private boolean						getPaid;
	@Column(scale = 2)
	private BigDecimal					profit;
	@Column(scale = 2)
	private BigDecimal					commissionAmount;
	private String						affiliateNetWork;
	@Max(value = 1)
	@Min(value = 0)
	@Column(scale = 3)
	private BigDecimal					cashBackPercent;

	private boolean						isPendingCBConf		= false;
	private boolean						isAvailCBConf		= false;

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

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public boolean isGetPaid() {
		return getPaid;
	}

	public void setGetPaid(boolean getPaid) {
		this.getPaid = getPaid;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getCashBackAmount() {
		return cashBackAmount;
	}

	public void setCashBackAmount(BigDecimal cashBackAmount) {
		this.cashBackAmount = cashBackAmount;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public UserCashBackRecordStatus getStatus() {
		return status;
	}

	public void setStatus(UserCashBackRecordStatus status) {
		this.status = status;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public String getAffiliateNetWork() {
		return affiliateNetWork;
	}

	public void setAffiliateNetWork(String affiliateNetWork) {
		this.affiliateNetWork = affiliateNetWork;
	}

	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public BigDecimal getCashBackPercent() {
		return cashBackPercent;
	}

	public void setCashBackPercent(BigDecimal cashBackPercent) {
		this.cashBackPercent = cashBackPercent;
	}

	public boolean isPendingCBConf() {
		return isPendingCBConf;
	}

	public void setPendingCBConf(boolean isPendingCBConf) {
		this.isPendingCBConf = isPendingCBConf;
	}

	public boolean isAvailCBConf() {
		return isAvailCBConf;
	}

	public void setAvailCBConf(boolean isAvailCBConf) {
		this.isAvailCBConf = isAvailCBConf;
	}

}
