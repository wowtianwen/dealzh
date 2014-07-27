/**
 * all the categories are cached when the system start up.
 * 
 */
package com.junhong.forum.backingbean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.datamodel.LazyEventDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.Event;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.EventServiceSingleton;
import com.junhong.util.Timing;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
@Timing
public class EventBackingBean extends AbstractBacking {

    @Inject
    private Logger logger;

    // represent the category
    // selected when you edit
    // the category
    @Inject
    private Event selectedEvent;

    @Inject
    private UserEjb userEjb;

    @Inject
    private EventServiceSingleton eventEjb;

    @Inject
    private LazyEventDataModel lazyEventDataModel;

    // total number of record for paging
    int totalListSize = 0;

    /* ------------instance Variable-------------- */

    @PostConstruct
    public void initialize() {
	totalListSize = (int) eventEjb.getTotalEventCount();
    }

    /* -------------business logic----------------- */

    /**
	 * 
	 */
    public String loadEvent() {
	String idStr = (String) this.getFlash().get("id");
	long id = -1;
	id = Long.parseLong(idStr);
	selectedEvent = eventEjb.getEventById(id);
	// this.getFlash().put("selectedCategory", selectedForumCategory);
	return "editEvent";
    }

    /**
     * create the category
     * 
     * @return
     */
    public String createEvent() {
	User user = this.getCurrentUser();
	selectedEvent.setOwner(user);
	boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, selectedEvent);
	if (hasError) {
	    return null;
	} else {
	    return Constants.NavSuccess;
	}

    }

    /**
     * @return
     */
    public String updateEvent() {
	boolean hasError = this.processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedEvent);
	if (logger.isDebugEnabled()) {
	    logger.debug("update Event.................{}", selectedEvent.getDescription());

	}
	if (hasError) {
	    // authorization fail, then restore the category's parentcategory
	    eventEjb.init();
	    return null;
	} else {
	    return Constants.NavSuccess;
	}

    }

    /**
     * @param category
     */
    public String deleteEvent() {
	logger.debug("deleting Event id {}", this.selectedEvent.getId());
	processBusinessWithAuthorizationCheck(Constants.Action_DELETE, this.selectedEvent);
	return null;
    }

    public boolean isAdminOrOwner() {
	return true;
    }

    /* -------------getter/setter----------------- */

    public void checkUniqueueness(FacesContext context, UIComponent comp, Object value) {
	Event event = new Event();
	event.setName((String) value);
	event.setId(this.selectedEvent.getId());
	if (event.getName() != null && eventEjb.isEventExists(event)) {
	    FacesMessage message = Messages.getMessage("", "DuplicateEvent", null);
	    message.setSeverity(FacesMessage.SEVERITY_ERROR);
	    context.addMessage(comp.getClientId(), message);
	    context.renderResponse();
	}

    }

    @Override
    protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
	Event event = (Event) entity;
	if (action.equals(Constants.Action_UPDATE)) {
	    eventEjb.updateEvent(event);
	    eventEjb.init();
	}
	if (action.equals(Constants.Action_CREATE)) {
	    eventEjb.createEvent(event);
	    eventEjb.init();

	}

	if (action.equals(Constants.Action_DELETE)) {
	    eventEjb.deleteEvent(event);
	    eventEjb.init();
	}
    }

    public String assignEventOwner() {
	String userId = (String) this.getRequestMap().get(Constants.NEWUSERID);
	User user = userEjb.getUserByUserId(userId);
	selectedEvent.setOwner(user);
	processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedEvent);
	return null;

    }

    public Event getSelectedEvent() {
	return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
	this.selectedEvent = selectedEvent;
    }

    public LazyEventDataModel getLazyEventDataModel() {
	return lazyEventDataModel;
    }

    public void setLazyEventDataModel(LazyEventDataModel lazyEventDataModel) {
	this.lazyEventDataModel = lazyEventDataModel;
    }

    public int getTotalListSize() {
	return totalListSize;
    }

    public void setTotalListSize(int totalListSize) {
	this.totalListSize = totalListSize;
    }

}
