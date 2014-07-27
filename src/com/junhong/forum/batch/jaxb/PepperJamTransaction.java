package com.junhong.forum.batch.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.junhong.forum.common.javabean.PepperJamTransactionReportBean;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement(name = "response")
public class PepperJamTransaction {

	// XmlElement sets the name of the entities

	private ArrayList<PepperJamTransactionReportBean>	transactionReportBeanList;

	@XmlElement(name = "data")
	public ArrayList<PepperJamTransactionReportBean> getTransactionReportBeanList() {
		return transactionReportBeanList;
	}

	public void setTransactionReportBeanList(ArrayList<PepperJamTransactionReportBean> transactionReportBeanList) {
		this.transactionReportBeanList = transactionReportBeanList;
	}

}