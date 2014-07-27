/**
 * forum
 * zhanjung
 */
package com.junhong.forum.stats;

/**
 * @author zhanjung
 * 
 *         For thread Hit and rating
 */
public enum CacheType {

	THREAD("THEAD"), NEWS("NEWS");

	/* ------------instance Variable-------------- */
	private String name;

	private CacheType(String name) {
		this.name = name;
	}

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
