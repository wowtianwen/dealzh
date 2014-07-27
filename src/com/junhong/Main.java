package com.junhong;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.junhong.forum.batch.jaxb.Deals;
import com.junhong.forum.entity.ThreadStaging;

public class Main {

	private static final String BOOKSTORE_XML = "deals-test.xml";

	public static void main(String[] args) throws JAXBException, IOException {

		JAXBContext context = JAXBContext.newInstance(Deals.class);
		// get variables from our xml file, created before
		System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();
		Deals deals = (Deals) um.unmarshal(new FileReader("C:\\Users\\zhanjung\\Documents\\parttimeproject\\deal\\src\\com\\junhong\\deals-test.xml"));
		ArrayList<ThreadStaging> list = deals.getThreads();
		for (ThreadStaging thread : list) {
			System.out.println(thread.getSubject());
		}
	}
}
