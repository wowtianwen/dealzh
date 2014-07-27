/**
 * forum
 * zhanjung
 */
package com.junhong.forum.stats;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.interceptor.ExcludeDefaultInterceptors;

/**
 * @author zhanjung handle the number of viewed
 */
@ExcludeDefaultInterceptors
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Named
@ApplicationScoped
public class HitCache {

	@EJB
	private CacheManager hitManager;

	/* ------------instance Variable-------------- */
	private Map<CacheKey, AtomicInteger> HITS_CACHE = new ConcurrentHashMap<CacheKey, AtomicInteger>();

	/* -------------business logic----------------- */
	/**
	 * No need to be synchronized, because using atomicinteger.
	 * 
	 * @param hitKey
	 * @return
	 */
	@Lock(LockType.READ)
	public Integer incrementAndGet(CacheKey hitKey) {
		return getHit(hitKey).incrementAndGet();
	}

	/**
	 * get the value only
	 * 
	 * @param hitKey
	 * @return
	 */
	@Lock(LockType.READ)
	public AtomicInteger getHit(CacheKey hitKey) {
		AtomicInteger hitValue = HITS_CACHE.get(hitKey);
		if (hitValue == null) {
			int hits = populateHits(hitKey);
			HITS_CACHE.put(hitKey, new AtomicInteger(hits));
		}
		return HITS_CACHE.get(hitKey);
	}

	/**
	 * get intial hits from the db
	 */
	@Lock(LockType.READ)
	protected int populateHits(CacheKey hitKey) {
		return hitManager.getHits(hitKey);
	}

	/**
	 * write it to db
	 */
	@SuppressWarnings("unused")
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "*/10", hour = "*", persistent = false)
	private void writeToDb() {//
		Set<CacheKey> hitKeySet = HITS_CACHE.keySet();
		for (CacheKey hk : hitKeySet) {

			hitManager.updateHits(hk, HITS_CACHE.get(hk).intValue());

		}

		// clear the cache after writing it to the database
		HITS_CACHE.clear();

	}
	/* -------------getter/setter----------------- */
}
