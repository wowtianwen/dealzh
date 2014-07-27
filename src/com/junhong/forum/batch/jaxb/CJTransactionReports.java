package com.junhong.forum.batch.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement(name = "cj-api")
public class CJTransactionReports {

	private CJCommission	cjCommission;

	@XmlElement(name = "commissions")
	public CJCommission getCjCommission() {
		return cjCommission;
	}

	public void setCjCommission(CJCommission cjCommission) {
		this.cjCommission = cjCommission;
	}

}