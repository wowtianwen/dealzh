package com.junhong.forum.entity;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Affilicate
 * 
 */
@Entity
@Dependent
@Table(name = "affiliate")
@NamedQueries({ @NamedQuery(name = "affiliate.all", query = "select s from Affiliate as s") })
public class Affiliate extends AbstractEntity implements Serializable {
	private static final long	serialVersionUID	= -1594763091416514941L;
	@NotNull
	@Size(min = 1, message = "{com.junhong.forum.entity.store.name}")
	private String				name;
	@NotNull
	@Size(min = 1)
	private String				publishId;
	private String				token;
	private String				urlPattern;
	private String				promLinkWSEndPoint;
	private String				transformXSL;
	private String				transactionReportWSEndPoint;

	public Affiliate() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublishId() {
		return this.publishId;
	}

	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrlPattern() {
		return this.urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + super.getId();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean defaultValue = false;
		if (this.getClass().equals(obj.getClass()) && this.getId() == ((Affiliate) obj).getId()) {
			defaultValue = true;
		}
		return defaultValue;

	}

	public String getPromLinkWSEndPoint() {
		return promLinkWSEndPoint;
	}

	public void setPromLinkWSEndPoint(String promLinkWSEndPoint) {
		this.promLinkWSEndPoint = promLinkWSEndPoint;
	}

	public String getTransformXSL() {
		return transformXSL;
	}

	public void setTransformXSL(String transformXSL) {
		this.transformXSL = transformXSL;
	}

	public String getTransactionReportWSEndPoint() {
		return transactionReportWSEndPoint;
	}

	public void setTransactionReportWSEndPoint(String transactionReportWSEndPoint) {
		this.transactionReportWSEndPoint = transactionReportWSEndPoint;
	}
}
