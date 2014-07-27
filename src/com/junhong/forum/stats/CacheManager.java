package com.junhong.forum.stats;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.service.ThreadEjb;
import com.junhong.news.ejb.NewsEjb;
import com.junhong.news.entity.News;

/**
 * Session Bean implementation class HitManager. <br>
 * For thread.hit and thread.rating. and other concurrent access properity of
 * thread
 */
@Stateless
@LocalBean
public class CacheManager {

	@Inject
	private ThreadEjb threadEjb;
	@Inject
	private NewsEjb newsEjb;

	/**
	 * Default constructor.
	 */
	public CacheManager() {
	}

	public int getHits(CacheKey hitKey) {
		ForumThread ft = null;
		if (hitKey.getHitType().equals(CacheType.THREAD)) {
			ft = threadEjb.getForumThreadById(hitKey.getId());
			if (ft == null) {
				return 0;
			}

			return ft.getNumberOfView();
		} else if (hitKey.getHitType().equals(CacheType.NEWS)) {
			News news = newsEjb.getNewsById(hitKey.getId());
			if (news == null) {
				return 0;
			}

			return news.getNumOfViews();
		}
		return 0;

	}

	public void updateHits(CacheKey hitKey, int numOfHits) {
		if (hitKey.getHitType().equals(CacheType.THREAD)) {
			// even though the same thread might be updated at the same time as
			// the following run. but it does not has been lock it because no
			// body
			// else will lock number of views for that thread
			threadEjb.updateNumOfView(hitKey.getId(), numOfHits);
		}
		if (hitKey.getHitType().equals(CacheType.NEWS)) {
			// even though the same news might be updated at the same time as
			// the following run. but it does not have to be locked because no
			// body
			// else will lock number of views for that thread
			newsEjb.updateNumOfView(hitKey.getId(), numOfHits);
		}

	}

	public int getRatings(CacheKey hitKey) {
		ForumThread ft = null;
		if (hitKey.getHitType().equals(CacheType.THREAD)) {
			ft = threadEjb.getForumThreadById(hitKey.getId());
			if (ft == null) {
				return 0;
			}

			return ft.getRating();
		}
		return 0;

	}

	public void updateVotes(CacheKey hitKey, int votes, int rating) {
		if (hitKey.getHitType().equals(CacheType.THREAD)) {
			threadEjb.updateVotesNRating(hitKey.getId(), votes, rating);
		}
		if (hitKey.getHitType().equals(CacheType.NEWS)) {
			newsEjb.updateVotes(hitKey.getId(), votes);
		}

	}

	public int getVotes(CacheKey hitKey) {
		ForumThread ft = null;
		if (hitKey.getHitType().equals(CacheType.THREAD)) {
			ft = threadEjb.getForumThreadById(hitKey.getId());
			if (ft == null) {
				return 0;
			}
			return ft.getVotes();
		}

		if (hitKey.getHitType().equals(CacheType.NEWS)) {
			News news = null;
			news = newsEjb.getNewsById(hitKey.getId());
			if (news == null) {
				return 0;
			}

			return news.getVotes();
		}
		return 0;

	}


}
