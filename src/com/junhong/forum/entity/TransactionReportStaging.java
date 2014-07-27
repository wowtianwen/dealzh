package com.junhong.forum.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Null;

import com.junhong.forum.common.RecordStatus;

@Entity
@Dependent
public class TransactionReportStaging extends AbstractEntity {
	private static final long	serialVersionUID	= -6738394333054938043L;
	private String				transactionId;
	private String				affiliateNetwork;
	@Column(scale = 2)
	private BigDecimal			transactionAmount;
	private Date				transactionDate;
	private String				transactionUserId;
	private String				itemId;
	private String				itemName;
	@Column(scale = 2)
	private BigDecimal			commissionAmount;
	@Null
	@Column(scale = 2)
	private BigDecimal			userCashbackAmount;
	@Enumerated(EnumType.STRING)
	private RecordStatus		recordStatus;
	private String				advertiserId;
	private String				advertiserName;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAffiliateNetwork() {
		return affiliateNetwork;
	}

	public void setAffiliateNetwork(String affiliateNetwork) {
		this.affiliateNetwork = affiliateNetwork;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionUserId() {
		return transactionUserId;
	}

	public void setTransactionUserId(String transactionUserId) {
		this.transactionUserId = transactionUserId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public RecordStatus getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(RecordStatus recordStatus) {
		this.recordStatus = recordStatus;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public BigDecimal getUserCashbackAmount() {
		return userCashbackAmount;
	}

	public void setUserCashbackAmount(BigDecimal userCashbackAmount) {
		this.userCashbackAmount = userCashbackAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}

}
