package com.junhong.forum.common;

public enum UserCashBackRecordStatus {

	PENDING("PENDING"), AVAILABLE("AVAILABLE");

	private String	value;

	private UserCashBackRecordStatus(String recordStatus) {
		this.value = recordStatus;
	}

}
