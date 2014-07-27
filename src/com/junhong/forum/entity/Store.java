package com.junhong.forum.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Dependent
@Entity
@Table(name = "store")
@NamedQueries({ @NamedQuery(name = "store.all", query = "select s from Store as s") })
public class Store extends AbstractEntity {
	private static final long	serialVersionUID	= 1141736225948899113L;
	@NotNull
	@Size(min = 1, message = "{com.junhong.forum.entity.store.name}")
	private String				name;
	private String				storePicturePathURL;
	private String				website;
	@NotNull
	@Size(min = 1, max = 1000)
	@Column(length = 1000)
	private String				storeDesc;
	private int					rating;									// up
																			// to
																			// 10
																			// stars
	@NotNull
	@Size(min = 1, message = "{com.junhong.forum.entity.store.domain}")
	private String				domain;

	@ManyToOne
	private Affiliate			affiliate;

	@Transient
	private int					affiliateId;
	private String				redirectLinkId;
	@OneToMany(mappedBy = "store")
	private List<ForumThread>	deals;

	// store Deal auto populate
	private String				sourceXmlURL;
	private String				transformXSL;
	@Lob
	private String				targetXML;
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateTime;
	private int					updateFrequency;
	@Column(length = 3000)
	// up to x% for x%
	private String				cashBackDesc;
	// value has to be [x]x%
	@Max(value = 1)
	@Min(value = 0)
	@Column(scale = 3)
	private BigDecimal			cashBackPercent;
	// should be affiliate link, if notapproved yet, then it will be generallink
	private String				genericStoreLink;

	private String				bfAdScan;
	private String				advertiserId;
	// the order of popular stores, Zero means not popular. the higher the more
	// popular
	private int					popularOrder;

	/* ------------get/setter--------------- */
	public String getDomain() {
		return domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Affiliate affiliate) {
		this.affiliate = affiliate;
	}

	public String getRedirectLinkId() {
		return redirectLinkId;
	}

	public void setRedirectLinkId(String redirectLinkId) {
		this.redirectLinkId = redirectLinkId;
	}

	public int getAffiliateId() {
		return affiliateId;
	}

	public void setAffiliateId(int affiliateId) {
		this.affiliateId = affiliateId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getStoreDesc() {
		return storeDesc;
	}

	public void setStoreDesc(String storeDesc) {
		this.storeDesc = storeDesc;
	}

	public String getStorePicturePathURL() {
		return storePicturePathURL;
	}

	public void setStorePicturePathURL(String storePicturePathURL) {
		this.storePicturePathURL = storePicturePathURL;
	}

	public List<ForumThread> getDeals() {
		return deals;
	}

	public void setDeals(List<ForumThread> deals) {
		this.deals = deals;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getSourceXmlURL() {
		return sourceXmlURL;
	}

	public void setSourceXmlURL(String sourceXmlURL) {
		this.sourceXmlURL = sourceXmlURL;
	}

	public String getTransformXSL() {
		return transformXSL;
	}

	public void setTransformXSL(String transformXSL) {
		this.transformXSL = transformXSL;
	}

	public String getTargetXML() {
		return targetXML;
	}

	public void setTargetXML(String targetXML) {
		this.targetXML = targetXML;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCashBackDesc() {
		return cashBackDesc;
	}

	public void setCashBackDesc(String cashBackDesc) {
		this.cashBackDesc = cashBackDesc;
	}

	public String getGenericStoreLink() {
		return genericStoreLink;
	}

	public void setGenericStoreLink(String genericStoreLink) {
		this.genericStoreLink = genericStoreLink;
	}

	public String getBfAdScan() {
		return bfAdScan;
	}

	public void setBfAdScan(String bfAdScan) {
		this.bfAdScan = bfAdScan;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}

	public BigDecimal getCashBackPercent() {
		return cashBackPercent;
	}

	public void setCashBackPercent(BigDecimal cashBackPercent) {
		this.cashBackPercent = cashBackPercent;
	}

	public int getPopularOrder() {
		return popularOrder;
	}

	public void setPopularOrder(int popularOrder) {
		this.popularOrder = popularOrder;
	}

}
