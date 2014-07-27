/**
 * forum
 * zhanjung
 */
package com.junhong.message.domain;

import java.util.Comparator;

/**
 * @author zhanjung
 * 
 */
public class MessageComparator implements Comparator<Message> {

	@Override
	public int compare(Message o1, Message o2) {
		// TODO Auto-generated method stub

		return o1.getCreateTime().compareTo(o2.getCreateTime());
	}
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
}
