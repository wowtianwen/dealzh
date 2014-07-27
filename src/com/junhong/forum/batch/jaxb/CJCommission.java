package com.junhong.forum.batch.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.junhong.forum.common.javabean.CJTransactionReportBean;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement()
public class CJCommission {

	// XmlElement sets the name of the entities

	private ArrayList<CJTransactionReportBean>	transactionRecord;

	@XmlElement(name = "commission")
	public ArrayList<CJTransactionReportBean> getTransactionRecord() {
		return transactionRecord;
	}

	public void setTransactionRecord(ArrayList<CJTransactionReportBean> transactionRecord) {
		this.transactionRecord = transactionRecord;
	}

}