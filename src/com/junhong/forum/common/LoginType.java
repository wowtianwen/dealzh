package com.junhong.forum.common;

public enum LoginType {
	SELF("SELF"), FACEBOOK("FACEBOOK"), GOOGLE("GOOGLE"), WEIBO("WEIBO"), QQ("QQ");

	private String	type;

	private LoginType(String type) {
		this.type = type;
	}

}
