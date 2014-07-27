package com.junhong.forum.common.javabean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SkimLinksTransactionReportBean {
	@XmlElement(name = "commissionID")
	private String	commissionId;
	@XmlElement(name = "date")
	private String	eventDate;
	@XmlElement(name = "publisherID")
	private String	publisherId;
	@XmlElement(name = "domainID")
	private String	domainID;
	@XmlElement(name = "merchantID")
	private String	merchantID;

	@XmlElement(name = "commissionValue")
	private String	commissionValue;

	@XmlElement(name = "orderValue")
	private String	orderValue;
	// This will be 'active' if the commission is still valid, otherwise 'cancelled'.
	@XmlElement(name = "status")
	private String	status;
	// Can be "unknown", "lead", "sale", "cpc" or "performance" if the commission is a performance incentive commission.
	@XmlElement(name = "commissionType")
	private String	commissionType;
	@XmlElement(name = "customID")
	private String	customID;
	@XmlElement(name = "url")
	private String	url;

	public String getCommissionId() {
		return commissionId;
	}

	public void setCommissionId(String commissionId) {
		this.commissionId = commissionId;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getDomainID() {
		return domainID;
	}

	public void setDomainID(String domainID) {
		this.domainID = domainID;
	}

	public String getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}

	public String getCommissionValue() {
		return commissionValue;
	}

	public void setCommissionValue(String commissionValue) {
		this.commissionValue = commissionValue;
	}

	public String getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(String orderValue) {
		this.orderValue = orderValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}

	public String getCustomID() {
		return customID;
	}

	public void setCustomID(String customID) {
		this.customID = customID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
