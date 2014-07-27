package com.junhong.forum.common.javabean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PepperJamTransactionReportBean {

	@XmlElement(name = "transaction_id")
	private String	transactionId;
	@XmlElement(name = "order_id")
	private String	orderId;
	@XmlElement(name = "commission")
	private String	commission;
	@XmlElement(name = "sale_amount")
	private String	transactionAmount;
	@XmlElement(name = "creative_type")
	private String	creativeType;
	@XmlElement(name = "type")
	private String	transactionType;
	@XmlElement(name = "date")
	private String	transactionDate;
	@XmlElement(name = "status")
	private String	transactionStatus;
	@XmlElement(name = "sub_type")
	private String	subType;
	@XmlElement(name = "sid")
	private String	sid;
	@XmlElement(name = "program_name")
	private String	advertiserName;
	@XmlElement(name = "program_id")
	private String	advertiserId;

	public boolean isNull() {
		boolean result = false;
		if (this.transactionId == null && this.orderId == null && this.commission == null && this.transactionAmount == null
				&& this.creativeType == null && this.transactionType == null && this.transactionDate == null && this.transactionStatus == null
				&& this.subType == null && this.sid == null && this.advertiserName == null && this.advertiserId == null) {
			result = true;
		}
		return result;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getCreativeType() {
		return creativeType;
	}

	public void setCreativeType(String creativeType) {
		this.creativeType = creativeType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
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
