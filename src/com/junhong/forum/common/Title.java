package com.junhong.forum.common;
/**
 * represent the user's title
 * @author zhanjung
 *
 */

public enum Title {

	BASIC("BASIC"), INTERMEDIATE("INTERMEDIATE"), SENIOR("SENIOR"), EXPERT("EXPERT");

	private String name;

	private Title(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
