package com.junhong.forum.common;

public enum ThreadType {

	REGULAR("REGULAR"), ANNOUNCE("ANNOUNCE");

	private String	type;

	private ThreadType(String type) {
		this.type = type;
	}

}
