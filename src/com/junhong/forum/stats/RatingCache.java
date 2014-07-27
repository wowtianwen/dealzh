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
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.interceptor.ExcludeDefaultInterceptors;

/**
 * @author zhanjung handle the thread votes and Rating
 * 
 */
@ExcludeDefaultInterceptors
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@Lock(LockType.READ)
@Named
@ApplicationScoped
public class RatingCache {

	@EJB
	private CacheManager hitManager;

	/* ------------instance Variable-------------- */
	private Map<CacheKey, AtomicInteger> VOTE_CACHE = new ConcurrentHashMap<CacheKey, AtomicInteger>();

	private Map<CacheKey, AtomicInteger> RATING_CACHE = new ConcurrentHashMap<CacheKey, AtomicInteger>();

	/* -------------business logic----------------- */
	/**
	 * No need to be synchronized, because using atomicinteger.
	 * 
	 * @param cacheKey
	 * @return
	 */
	@Lock(LockType.READ)
	public int incrementAndGet(CacheKey cacheKey, int rating) {
		getVote(cacheKey).incrementAndGet();
		int resultRating = getRating(cacheKey).addAndGet(rating);
		return resultRating;
	}

	/**
	 * get the value only
	 * 
	 * @param hitKey
	 * @return
	 */
	@Lock(LockType.READ)
	public AtomicInteger getVote(CacheKey cacheKey) {
		AtomicInteger voteValue = VOTE_CACHE.get(cacheKey);
		if (voteValue == null) {
			int votes = getIntialVote(cacheKey);
			VOTE_CACHE.put(cacheKey, new AtomicInteger(votes));
		}
		return VOTE_CACHE.get(cacheKey);
	}

	@Lock(LockType.READ)
	public AtomicInteger getRating(CacheKey cacheKey) {
		AtomicInteger ratingValue = RATING_CACHE.get(cacheKey);
		if (ratingValue == null) {
			int rating = getInitialRating(cacheKey);
			RATING_CACHE.put(cacheKey, new AtomicInteger(rating));
		}
		return RATING_CACHE.get(cacheKey);
	}

	/**
	 * get intial hits from the db
	 */
	@Lock(LockType.READ)
	protected int getIntialVote(CacheKey cacheKey) {
		return hitManager.getVotes(cacheKey);
	}

	@Lock(LockType.READ)
	protected int getInitialRating(CacheKey cacheKey) {
		return hitManager.getRatings(cacheKey);
	}

	/**
	 * write it to db
	 */
	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/25", hour = "*",persistent = false)
	public void syncWithDb() {
		Set<CacheKey> voteKeySet = VOTE_CACHE.keySet();
		int vote = 0;
		int rating = 0;
		for (CacheKey hk : voteKeySet) {
			if (VOTE_CACHE.get(hk) != null) {
				vote = VOTE_CACHE.get(hk).intValue();
			}
			if (RATING_CACHE.get(hk) != null) {
				rating = RATING_CACHE.get(hk).intValue();
			}
			hitManager.updateVotes(hk, vote, rating);
		}
		// clear the cache after writing it to the database
		VOTE_CACHE.clear();
		RATING_CACHE.clear();

	}

}
