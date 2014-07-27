package com.junhong.auth.entity;

public enum UserStatus {

	PENDING("PENDING"), ACTIVE("ACTIVE"), LOCKED("LOCKED");

	private UserStatus(String status) {
		this.status = status;
	}

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
