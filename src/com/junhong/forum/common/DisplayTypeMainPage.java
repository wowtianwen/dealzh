/**
 * 
 */
package com.junhong.forum.common;

/**
 * @author zhanjung
 *         Display type for the website index page.
 */
public enum DisplayTypeMainPage {

	// NONE: DO NOT DISPLAY ON THE INDEX PAGE
	// REGULAR: DISPLA AS INDEPENDENT SECTION ON THE MAIN INDEX PAGE
	// MIXED: DISPLAY AS MIXED WITH OTHER CATEGORIES.
	NONE("NONE"), REGULAR("REGULAR"), MIXED("MIXED");

	private String	value;

	private DisplayTypeMainPage(String displayType) {
		this.value = displayType;
	}

}
