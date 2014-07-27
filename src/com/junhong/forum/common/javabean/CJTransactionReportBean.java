package com.junhong.forum.common.javabean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CJTransactionReportBean {

	@XmlElement(name = "order-id")
	private String	orderId;
	@XmlElement(name = "event-date")
	private String	eventDate;
	private String	postingDate;
	@XmlElement(name = "locking-date")
	private String	lockingDate;
	@XmlElement(name = "action-status")
	private String	actionStatus;
	@XmlElement(name = "action-type")
	private String	actionType;
	@XmlElement(name = "aid")
	private String	aid;
	@XmlElement(name = "commission-id")
	private String	commissionId;
	@XmlElement(name = "country")
	private String	country;
	@XmlElement(name = "website-id")
	private String	websiteId;
	@XmlElement(name = "action-tracker-id")
	private String	actionTrackerId;
	@XmlElement(name = "action-tracker-name")
	private String	actionTrackerName;
	@XmlElement(name = "cid")
	private String	cid;
	@XmlElement(name = "original")
	private String	original;
	@XmlElement(name = "original-action-id")
	private String	originalActionId;
	@XmlElement(name = "commission-amount")
	private String	commissionAmount;
	@XmlElement(name = "advertiser-name")
	private String	advertiserName;
	@XmlElement(name = "sid")
	private String	sid;
	@XmlElement(name = "sale-amount")
	private String	salesAmount;
	@XmlElement(name = "order-discount")
	private String	orderDiscount;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

	public String getLockingDate() {
		return lockingDate;
	}

	public void setLockingDate(String lockingDate) {
		this.lockingDate = lockingDate;
	}

	public String getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getCommissionId() {
		return commissionId;
	}

	public void setCommissionId(String commissionId) {
		this.commissionId = commissionId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}

	public String getActionTrackerId() {
		return actionTrackerId;
	}

	public void setActionTrackerId(String actionTrackerId) {
		this.actionTrackerId = actionTrackerId;
	}

	public String getActionTrackerName() {
		return actionTrackerName;
	}

	public void setActionTrackerName(String actionTrackerName) {
		this.actionTrackerName = actionTrackerName;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getOriginalActionId() {
		return originalActionId;
	}

	public void setOriginalActionId(String originalActionId) {
		this.originalActionId = originalActionId;
	}

	public String getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(String commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(String salesAmount) {
		this.salesAmount = salesAmount;
	}

	public String getOrderDiscount() {
		return orderDiscount;
	}

	public void setOrderDiscount(String orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

}
