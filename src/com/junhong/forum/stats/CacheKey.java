/**
 * forum
 * zhanjung
 */
package com.junhong.forum.stats;

/**
 * @author zhanjung
 * 
 */
public class CacheKey {
	/* ------------instance Variable-------------- */

	private int id;
	private CacheType cacheType;

	/**
	 * @param id
	 * @param hitType
	 */
	public CacheKey(int id, CacheType hitType) {
		super();
		this.id = id;
		this.cacheType = hitType;
	}

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
	public int getId() {
		return id;
	}

	public CacheType getHitType() {
		return cacheType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHitType(CacheType hitType) {
		this.cacheType = hitType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cacheType == null) ? 0 : cacheType.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheKey other = (CacheKey) obj;
		if (cacheType != other.cacheType)
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
