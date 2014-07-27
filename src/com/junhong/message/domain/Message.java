package com.junhong.message.domain;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.junhong.auth.entity.User;
import com.junhong.forum.entity.AbstractEntity;

@Dependent
@Entity()
@Table(name = "message")
@SqlResultSetMapping(name = "LongResut", columns = { @ColumnResult(name = "totalNum") })
public class Message extends AbstractEntity {

	private static final long					serialVersionUID	= -6997314160878513835L;

	private String								toUserids			= new String();
	@Transient
	private boolean								selected			= false;

	/* ------------instance Variable-------------- */
	private String								subject;
	@Column(columnDefinition = "LONGTEXT")
	private String								content;
	@Enumerated(EnumType.STRING)
	private MessageStatus						outBoxMessstatus	= MessageStatus.SENT;		// Archieved/ deleted/Sent
	// private boolean isDeletedFromSenter = false;
	// private boolean isDeletedFromAllReceivers = false;

	private boolean								isGroupMessage		= false;

	@ManyToOne
	private User								from;

	/*
	 * this represent this message's status for each recipient
	 */
	@OneToMany(mappedBy = "message", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
	private Collection<RecipientMessageStatus>	recipientStatus;

	/*
	 * @ManyToOne private Notification replyTo;
	 */

	/*--------getter/setter-----------------------*/
	/**
	 * 
	 */
	public Message() {
		super();
	}

	/**
	 * @param subject
	 * @param content
	 */
	public Message(Message noti) {
		this.content = noti.getContent();
	}

	public String getContent() {
		return content;
	}

	public User getFrom() {
		return from;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public String getToUserids() {
		return toUserids;
	}

	public void setToUserids(String toUserids) {
		this.toUserids = toUserids;
	}

	public boolean isGroupMessage() {
		return isGroupMessage;
	}

	public void setGroupMessage(boolean isGroupMessage) {
		this.isGroupMessage = isGroupMessage;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public MessageStatus getOutBoxMessstatus() {
		return outBoxMessstatus;
	}

	public void setOutBoxMessstatus(MessageStatus outBoxMessstatus) {
		this.outBoxMessstatus = outBoxMessstatus;
	}

	/**
	 * @return the recipientStatus
	 */
	public Collection<RecipientMessageStatus> getRecipientStatus() {
		return recipientStatus;
	}

	/**
	 * @param recipientStatus
	 *            the recipientStatus to set
	 */
	public void setRecipientStatus(Collection<RecipientMessageStatus> recipientStatus) {
		this.recipientStatus = recipientStatus;
	}

}
