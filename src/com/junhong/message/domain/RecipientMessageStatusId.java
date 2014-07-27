package com.junhong.message.domain;

import java.io.Serializable;

public class RecipientMessageStatusId implements Serializable {

	private static final long	serialVersionUID	= 6654307588594082661L;
	private int					message;
	private int					user;

	public RecipientMessageStatusId() {

	}

	public RecipientMessageStatusId(int messageId, int userId) {
		this.message = messageId;
		this.user = userId;
	}

	/**
	 * @return the message
	 */
	public int getMessage() {
		return message;
	}

	/**
	 * @return the user
	 */
	public int getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + message;
		result = prime * result + user;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RecipientMessageStatusId other = (RecipientMessageStatusId) obj;
		if (message != other.message) {
			return false;
		}
		if (user != other.user) {
			return false;
		}
		return true;
	}

}
