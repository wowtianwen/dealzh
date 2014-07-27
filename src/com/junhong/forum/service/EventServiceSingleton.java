/**
 * forum
 * zhanjung
 */
package com.junhong.forum.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.dao.EventDAO;
import com.junhong.forum.entity.Event;
import com.junhong.forum.exceptions.AuthorizationFailException;

/**
 * all the categories are cached. any access to category is reading from the
 * cache.
 * 
 * @author zhanjung
 * 
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Singleton
@Lock(LockType.READ)
public class EventServiceSingleton implements Serializable {

    /* ------------instance Variable-------------- */

    // cache to store the numOfThread,
    // THREAD SAFE
    // Map<categoryName, ForumCategoy>
    private Map<String, Event> eventOfThreadMap = new ConcurrentHashMap<String, Event>();
    private List<Event> allEvents = new ArrayList<Event>();

    @Inject
    Logger logger;

    @Inject
    EventDAO eventDAO;

    /* -------------business logic----------------- */
    @PostConstruct
    public void init() {
	logger.info("Start ------ " + "fetch all the Event ");
	allEvents = eventDAO.findAll(Event.class);
	logger.info("End ------ " + "fetch all the Event ");

    }

    @PreDestroy
    @Timeout
    @Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "0/20", hour = "*",persistent = false)
    public void updateNumberOfThead() {
	eventDAO.updateNumberOfThead(eventOfThreadMap.values());
    }

    /**
     * category is return from the single Ejb class, it should return the same
     * one all the time
     * 
     * @param categoryName
     * @param numOfThread
     */
    @Asynchronous
    public void updateNumOfThread(Event event, int delta) {
	event.setNumOfThread(event.getNumOfThread() + delta);
    }

    @Lock(LockType.READ)
    public List<Event> getAllEvents() {
	return allEvents;
    }

    @Lock(LockType.READ)
    public Event getEventById(long eventId) {
	for (Event e : allEvents) {
	    if (eventId == e.getId()) {
		return e;
	    }
	}
	return null;
    }

    public List<Event> getAllEvents(int start, int size) {
	String hql = "select e from Event e ";
	return eventDAO.findByHQL(hql, Event.class, start, size);
    }

    @Lock(LockType.READ)
    public boolean isEventExists(Event event) {
	int count = 0;
	// check for new category
	if (event.getId() == 0) {
	    for (Event fc : allEvents) {
		if (fc.equals(event)) {
		    count++;
		}
	    }
	}
	// check for the update of category
	else {
	    for (Event fc : allEvents) {
		if (event.getId() != fc.getId() && fc.equals(event)) {
		    count++;
		}
	    }
	}
	if (count > 0) {
	    return true;
	}
	return false;
    }

    @Lock(LockType.READ)
    @Role(RoleType.SYSADMIN)
    @Interceptors(AuthorizationInterceptor.class)
    public int getTotalEventCount() {
	return allEvents == null ? 0 : allEvents.size();
    }

    @Lock(LockType.WRITE)
    @Role(RoleType.SYSADMIN)
    @Interceptors(AuthorizationInterceptor.class)
    public void createEvent(Event event) {
	eventDAO.create(event);
	init();

    }

    /**
     * @param event
     */
    @Lock(LockType.WRITE)
    @Role(RoleType.SYSADMIN)
    @Interceptors(AuthorizationInterceptor.class)
    public void updateEvent(Event event) throws AuthorizationFailException {
	eventDAO.update(event);
	// update the Category List
	init();
    }

    /**
     * @param forumCategory
     */
    @Lock(LockType.WRITE)
    @Role(RoleType.SYSADMIN)
    @Interceptors(AuthorizationInterceptor.class)
    public void deleteEvent(Event event) throws AuthorizationFailException {
	eventDAO.delete(event);
	init();
    }

    /**
     * refresh from the db
     * 
     * @param event
     * @return
     */
    public Event refreshCategory(Event event) {
	return eventDAO.findById(Event.class, event.getId());
    }

}
