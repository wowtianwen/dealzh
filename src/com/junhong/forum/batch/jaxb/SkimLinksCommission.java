package com.junhong.forum.batch.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.junhong.forum.common.javabean.SkimLinksTransactionReportBean;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement(name = "commissions")
public class SkimLinksCommission {

	// XmlElement sets the name of the entities

	private ArrayList<SkimLinksTransactionReportBean>	transactionRecord;

	@XmlElement(name = "commission")
	public ArrayList<SkimLinksTransactionReportBean> getTransactionRecord() {
		return transactionRecord;
	}

	public void setTransactionRecord(ArrayList<SkimLinksTransactionReportBean> transactionRecord) {
		this.transactionRecord = transactionRecord;
	}

}