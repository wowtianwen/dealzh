package com.junhong.forum.common;

public enum ThreadStatus {

	PENDINGREVIEW("PENDINGREVIEW"), REJECTED("REJECTED"), APPROVED("APPROVED");

	private String	status;

	private ThreadStatus(String status) {
		this.status = status;
	}

}
