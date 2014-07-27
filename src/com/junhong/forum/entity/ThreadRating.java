package com.junhong.forum.entity;

import javax.enterprise.context.Dependent;
import javax.persistence.Entity;

/**
 * this table is for track if user already voted the rating for the given thread
 * it concatenate user's id and thread id to generate unique key.
 * 
 * @author zhanjung
 * 
 */
@Dependent
@Entity
public class ThreadRating extends AbstractEntity {

	/* ------------instance Variable-------------- */
	public ThreadRating(String userIdThreadId) {
		this.userIdThreadId = userIdThreadId;
	}

	public ThreadRating() {

	}

	//
	private String	userIdThreadId;

	public String getUserIdThreadId() {
		return userIdThreadId;
	}

	public void setUserIdThreadId(String userIdThreadId) {
		this.userIdThreadId = userIdThreadId;
	}

}
