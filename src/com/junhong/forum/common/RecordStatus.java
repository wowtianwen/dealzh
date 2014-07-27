package com.junhong.forum.common;

public enum RecordStatus {

	PENDING("PENDING"), PROCESSED("PROCESSED"), ERROR("ERROR");

	private String value;

	private RecordStatus(String recordStatus) {
		this.value = recordStatus;
	}

}
