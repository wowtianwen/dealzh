package com.junhong.forum.batch.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.junhong.forum.entity.ThreadStaging;

//This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement()
public class Deals {

	// XmlElement sets the name of the entities

	private ArrayList<ThreadStaging>	threads;

	@XmlElement(name = "thread")
	public ArrayList<ThreadStaging> getThreads() {
		return threads;
	}

	public void setThreads(ArrayList<ThreadStaging> threads) {
		this.threads = threads;
	}

}