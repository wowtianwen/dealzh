package com.junhong.forum.entity;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.junhong.auth.entity.User;

@Dependent
@Entity
@Table(name = "reply")
@NamedQueries({ @NamedQuery(name = "forumreply", query = "select r from ForumReply as r") })
public class ForumReply extends AbstractEntity {

	private static final long serialVersionUID = -4413532915511306785L;
	/* ------------instance Variable-------------- */
	private String subject; // not
							// sure
							// if
							// needed
	@Column(length = 60000)
	private String content;

	@Transient
	boolean editable;

	@ManyToOne
	@JoinColumn(name = "owner_Id")
	private User owner;
	@ManyToOne
	@JoinColumn(name = "thread_Id")
	private ForumThread thread;
	@ManyToOne
	@JoinColumn(name = "store_Id")
	private Store store;

	/* -------------Constructor----------------- */

	public ForumReply() {

	}

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public ForumThread getThread() {
		return thread;
	}

	public void setThread(ForumThread thread) {
		this.thread = thread;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

}
