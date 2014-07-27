/**
 * 
 */
package com.junhong.forum.common;

/**
 * @author zhanjung
 *         Display type for the news index page.
 */
public enum DisplayType {

	TAB("TAB"), REGULAR("REGULAR");

	private String	value;

	private DisplayType(String displayType) {
		this.value = displayType;
	}

}
