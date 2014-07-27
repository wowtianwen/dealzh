/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import java.util.Collection;

import javax.persistence.Query;

import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.entity.Event;

/**
 * @author zhanjung
 * 
 */
public class EventDAO extends EntityDAOImpl<Event> {
    /* ------------instance Variable-------------- */

    /* -------------business logic----------------- */

    public long getTotalCount() {
	return this.getEm().createQuery(QueryConstants.forumcategory_total_count, Long.class).getSingleResult();

    }

    public long getTotalNumberOfThead(Event category) {
	// TODO getTotalNumberOfThead in ForumCategoryDAO
	return 0;

    }

    /**
     * update the number of thread for each category in the given list
     * 
     * @param eventList
     */
    public void updateNumberOfThead(Collection<Event> eventList) {

	String updateQuery = "update Event c set  c.numOfThread=:num where c.id=:Id";
	Query query = this.getEm().createQuery(updateQuery);
	for (Event fc : eventList) {
	    query.setParameter("num", fc.getNumOfThread()).setParameter("Id", fc.getId()).executeUpdate();

	}

    }
    /* -------------getter/setter----------------- */
}
