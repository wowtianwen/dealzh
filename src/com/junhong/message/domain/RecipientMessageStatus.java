package com.junhong.message.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.junhong.auth.entity.User;

@Entity
@Table(name = "message_user")
//@NamedQuery(name = "RecipientMessageStatus.updateRecipientMessageStatus", query = "update RecipientMessageStatus rms  set rms.status=:messStatus where rms.message.id  IN(:messageIds) and rms.user=:user")
@IdClass(RecipientMessageStatusId.class)
public class RecipientMessageStatus {

	@Id
	@ManyToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
	@JoinColumn(name = "mess_id")
	private Message			message;

	@Id
	@ManyToOne()
	@JoinColumn(name = "user_id")
	private User			user;

	@Enumerated(EnumType.STRING)
	private MessageStatus	status;

	public RecipientMessageStatus() {

	}

	public RecipientMessageStatus(Message message, User user) {
		this.message = message;
		this.user = user;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the status
	 */
	public MessageStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(MessageStatus status) {
		this.status = status;
	}

}
