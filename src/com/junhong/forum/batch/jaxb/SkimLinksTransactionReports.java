package com.junhong.forum.batch.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement(name = "skimlinksAccount")
public class SkimLinksTransactionReports {

	private SkimLinksCommission	skimLinksCommission;

	private String				total;

	public SkimLinksCommission getSkimLinksCommission() {
		return skimLinksCommission;
	}

	@XmlElement(name = "commissions")
	public void setSkimLinksCommission(SkimLinksCommission skimLinksCommission) {
		this.skimLinksCommission = skimLinksCommission;
	}

	public String getTotal() {
		return total;
	}

	@XmlElement(name = "total")
	public void setTotal(String total) {
		this.total = total;
	}

}