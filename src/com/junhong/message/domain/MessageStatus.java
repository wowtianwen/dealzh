/**
 * forum
 * zhanjung
 */
package com.junhong.message.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * For Mesage Outbox: its value could be SENT,ARCHIEVED,DELETED,saved
 * Inbox Message: NEW, READ,ARCHIEVED
 * Archieved Message: its value is Archieved only
 * 
 * @author zhanjung
 * 
 */
@Named
@ApplicationScoped
public enum MessageStatus {
	NEW("NEW"), READ("READ"), REPLIED("REPLIED"), SENT("SENT"), ARCHIEVED("ARCHIEVED"), DELETED("DELETED"), DRAFT("DRAFT");

	private String	status;

	private String getStatus() {
		return status;
	}

	private void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param status
	 */
	private MessageStatus(String status) {
		this.status = status;
	}

}
